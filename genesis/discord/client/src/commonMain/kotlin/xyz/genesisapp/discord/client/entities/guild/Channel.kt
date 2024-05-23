package uninit.genesis.discord.client.entities.guild

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import uninit.common.compose.fytix.ComposableEventBus
import uninit.common.fytix.Err
import uninit.common.fytix.Ok
import uninit.common.getTimeInMillis
import uninit.genesis.discord.api.domain.DomainMessage
import uninit.genesis.discord.api.domain.UtcDateTime
import uninit.genesis.discord.api.types.AssetType
import uninit.genesis.discord.api.types.Snowflake
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.entities.Asset
import uninit.genesis.discord.client.enum.LogLevel
import uninit.genesis.discord.entities.guild.ApiChannel
import uninit.genesis.discord.entities.guild.ChannelType

class Channel(
    val genesisClient: GenesisClient,
    var id: Snowflake,
    var guildId: Snowflake,
    var position: Int,
    var name: String,
    var nsfw: Boolean,
    var parentId: Snowflake? = null,
    var type: ChannelType,
    var children: MutableList<Snowflake> = mutableListOf(),
    var isCollapsed: MutableState<Boolean> = mutableStateOf(false),
    val recipients: MutableList<User> = mutableListOf(),
    val icon: Asset? = null,
    val lastMessageId: Snowflake? = null,
) : ComposableEventBus("Channel$id") {
    val messages = mutableStateListOf<Message>()

    /**
     * Explicit typing for [emit] function, event is [Message]
     */
    fun emitMessageCreate(message: Message) = emit("MESSAGE_CREATE", message)

    /**
     * Explicit typing for [on] function, event is [Message]
     */
    fun onMessageCreate(block: Event<Message>.(Message) -> Unit) = on("MESSAGE_CREATE", block)

    /**
     * Explicit typing for [compositionLocalOn] function, event is [Message]
     */
    @Composable
    fun compositionOnMessageCreate(block: Event<Message>.(Message) -> Unit) =
        compositionLocalOn("MESSAGE_CREATE", block)

    /**
     * Explicit typing for [emit] function, event is [List<Message>]
     */
    fun emitMessageCreateBulk(messages: List<Message>) = emit("MESSAGE_CREATE_BULK", messages)

    /**
     * Explicit typing for [on] function, event is [List<Message>]
     */
    fun onMessageCreateBulk(block: Event<List<Message>>.(List<Message>) -> Unit) =
        on("MESSAGE_CREATE_BULK", block)

    /**
     * Explicit typing for [compositionLocalOn] function, event is [List<Message>]
     */
//    @Composable
//    fun compositionOnMessageCreateBulk(block: Event<List<Message>>.(List<Message>) -> Unit) =
//        compositionLocalOn("MESSAGE_CREATE_BULK", block)

    /**
     * Explicit typing for [emit] function, event is [Snowflake]
     */
    fun emitMessageDelete(messageId: Snowflake) = emit("MESSAGE_DELETE", messageId)

    /**
     * Explicit typing for [on] function, event is [Snowflake]
     */
    fun onMessageDelete(block: Event<Snowflake>.(Snowflake) -> Unit) = on("MESSAGE_DELETE", block)

    /**
     * Explicit typing for [compositionLocalOn] function, event is [Snowflake]
     */
//    @Composable
//    fun compositionOnMessageDelete(block: Event<Snowflake>.(Snowflake) -> Unit) =
//        compositionLocalOn("MESSAGE_DELETE", block)


    private fun addMessage(message: Message, isBulk: Boolean = false): Message {
        if (messages.find { it.id == message.id } == null) {
            messages.add(message)
            sort()
            if (!isBulk) emitMessageCreate(message)
        }
        return message
    }

    fun addApiMessages(domainMessages: List<DomainMessage>): List<Message> =
        addMessages(domainMessages.map { Message.fromApiMessage(it, genesisClient) })

    fun addMessages(messages: List<Message>): List<Message> {
        val added = mutableListOf<Message>()
        messages.forEach { added.add(addMessage(it, true)) }
        emitMessageCreateBulk(added)
        return added
    }

    fun deleteMessage(message: Message) = deleteMessage(message.id)

    fun deleteMessage(domainMessage: DomainMessage) = deleteMessage(domainMessage.id!!)

    fun deleteMessage(id: Snowflake) {
        val message = messages.find { it.id == id }
        if (message != null) {
            messages.remove(message)
            emitMessageDelete(id)
        }
    }

    val guild: Guild
        get() = genesisClient.guilds[guildId]!!

    internal fun sort() {
        messages.sortBy { it.id }
    }

    suspend fun fetchMessages(limit: Int = 50, after: Snowflake? = null): List<Message>? {
        return when (val res = genesisClient.rest.getMessages(id, limit, after)) {
            is Ok -> {
                return addApiMessages(res.value)
            }

            is Err -> {
                null
            }
        }
    }

    suspend fun sendMessage(content: String): Message? {
        val domainMessage = DomainMessage(
            content = content,
            channelId = id,
            nonce = getTimeInMillis() + UtcDateTime.DISCORD_EPOCH * 1000000
        )
        var message = Message.fromApiMessage(domainMessage, genesisClient)
        message.isSent = false
        addMessage(message)
        delay(3000L)
        return when (val res = genesisClient.rest.sendMessage(id, domainMessage)) {
            is Ok -> {
                messages.remove(message)
                message = Message.fromApiMessage(res.value, genesisClient)
                addMessage(message)
                message
            }

            is Err -> {
                if (genesisClient.logLevel >= LogLevel.ERROR) Napier.e(
                    "Error sending message",
                    Throwable(res.errorOrNull()?.message),
                    "Channel"
                )
                null
            }
        }
    }

    companion object {
        fun fromApiChannel(
            apiChannel: ApiChannel,
            guildId: Snowflake,
            genesisClient: GenesisClient
        ): Channel = Channel(
            genesisClient,
            id = apiChannel.id,
            guildId = guildId,
            position = apiChannel.position!!,
            name = apiChannel.name!!,
            nsfw = apiChannel.nsfw ?: false,
            parentId = apiChannel.parentId,
            type = apiChannel.type!!,
            recipients = apiChannel.recipients?.map { User.fromApiUser(it, genesisClient) }
                ?.toMutableList() ?: mutableListOf(),
            icon = apiChannel.icon?.let {
                Asset(
                    genesisClient,
                    it,
                    AssetType.Avatar,
                    apiChannel.recipients?.get(0)!!.id
                )
            },
            lastMessageId = apiChannel.lastMessageId
        )
    }
}