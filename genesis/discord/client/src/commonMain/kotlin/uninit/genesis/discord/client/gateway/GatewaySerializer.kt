package uninit.genesis.discord.client.gateway

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import uninit.genesis.discord.client.gateway.entities.GenericGatewayEvent

object GatewaySerializer : JsonContentPolymorphicSerializer<GenericGatewayEvent>(GenericGatewayEvent::class) {
    /**
     * Determines a particular strategy for deserialization by looking on a parsed JSON [element].
     */
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<GenericGatewayEvent> {
        TODO("Not yet implemented")
    }
}