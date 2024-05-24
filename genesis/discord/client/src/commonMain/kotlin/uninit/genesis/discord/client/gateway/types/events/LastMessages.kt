package uninit.genesis.discord.client.gateway.types.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uninit.genesis.discord.api.domain.DomainMessage
import uninit.genesis.discord.api.types.Snowflake

@Serializable
data class LastMessages(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val messages: List<DomainMessage>,
)