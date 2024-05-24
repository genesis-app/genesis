package uninit.genesis.discord.client.gateway.serializers

import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uninit.genesis.discord.api.domain.DomainMessage
import uninit.genesis.discord.client.gateway.types.EmptyGatewayEvent
import uninit.genesis.discord.client.gateway.types.GatewayEvent
import uninit.genesis.discord.client.gateway.types.events.LastMessages
import uninit.genesis.discord.client.gateway.types.events.Ready
import uninit.genesis.discord.client.gateway.entities.events.GatewayHello
import uninit.genesis.discord.client.gateway.types.iGatewayEvent

object GatewaySerializer : JsonContentPolymorphicSerializer<iGatewayEvent>(iGatewayEvent::class) {
    override fun selectDeserializer(element: JsonElement) =
        when (element.jsonObject["op"]!!.jsonPrimitive.int) {
            0 -> Opcode0Serializer
            1 -> EmptyGatewayEvent.serializer()
//            9 -> BooleanGatewayEvent.serializer() kotlin doesnt like this :(
            10 -> GatewayEvent.serializer(GatewayHello.serializer())
            else -> error("Unknown opcode: ${element.jsonObject["op"]!!.jsonPrimitive.int}")
        }
}

object Opcode0Serializer : JsonContentPolymorphicSerializer<iGatewayEvent>(iGatewayEvent::class) {
    override fun selectDeserializer(element: JsonElement) =
        when (element.jsonObject["t"]!!.jsonPrimitive.toString().replace("\"", "")) {
            "READY" -> GatewayEvent.serializer(Ready.serializer())
            "MESSAGE_CREATE" -> GatewayEvent.serializer(DomainMessage.serializer())
            "LAST_MESSAGES" -> GatewayEvent.serializer(LastMessages.serializer())
            else -> error("Unknown event: ${element.jsonObject["t"]!!.jsonPrimitive}")
        }
}