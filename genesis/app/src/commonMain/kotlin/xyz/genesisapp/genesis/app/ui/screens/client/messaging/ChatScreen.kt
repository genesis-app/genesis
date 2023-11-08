package xyz.genesisapp.genesis.app.ui.screens.client.messaging


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin
import xyz.genesisapp.discord.api.types.AssetType
import xyz.genesisapp.discord.api.types.Snowflake
import xyz.genesisapp.discord.api.types.toUrl
import xyz.genesisapp.discord.client.GenesisClient
import xyz.genesisapp.discord.client.entities.guild.Channel
import xyz.genesisapp.discord.client.entities.guild.Message
import xyz.genesisapp.genesis.app.data.DataStore
import xyz.genesisapp.genesis.app.ui.screens.EventScreen

@OptIn(ExperimentalResourceApi::class)
@Composable
fun message(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
                .clip(shape = CircleShape)
        ) {
            if (message.author.avatar != null) {
                KamelImage(
                    resource = asyncPainterResource(
                        message.author.avatar!!.toUrl(
                            AssetType.Avatar,
                            message.author.id,
                            128
                        )
                    ),
                    contentDescription = "Avatar",
                )
            } else {
                Image(
                    painter = painterResource("images/default/default_avatar_${message.author.id % 5}.png"),
                    contentDescription = "Avatar",
                )
            }
        }
        Column {
            Text(message.author.username)
            Text(
                message.content,
                color = if (message.isSent) MaterialTheme.colorScheme.onSurface else Color.Gray
            )
        }
    }
}

// Should be replaced with indexing
@Suppress()
class ChatScreen(
    private var channel: Channel
) : EventScreen() {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val koin = getKoin()
        val genesisClient = koin.get<GenesisClient>()
        val dataStore = koin.get<DataStore>()
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()


        val listState = rememberLazyListState()


        var numNewMessages by remember { mutableStateOf(0) }

        fun scrollToBottom(animated: Boolean = false) {
            if (channel.messages.isEmpty()) return
            scope.launch {
                try {
                    if (animated) listState.animateScrollToItem(channel.messages.size - 1)
                    else listState.scrollToItem(channel.messages.size - 1)
                } catch (_: Exception) {
                }
            }
        }

        var showMemberList by remember { mutableStateOf(false) }

        fun newMessage(isBulk: Boolean = false) {
            println("new message")
            val isAtBottom = !listState.canScrollForward
            if (isAtBottom) scrollToBottom()
            else if (!isBulk) numNewMessages++
        }

        if (channel.messages.isNotEmpty()) {
            scrollToBottom()
        } else {
            scope.launch {
                channel.fetchMessages(50)
            }
        }

        Events(
            dataStore.events.quietRegister<Snowflake>("CHANNEL_SELECT") {
                navigator.push(
                    ChatScreen(genesisClient.channels[it]!!)
                )
            },
            channel.quietRegister<Message>("MESSAGE_CREATE") { newMessage() },
            channel.quietRegister<List<Message>>("MESSAGE_CREATE_BULK") { newMessage(true) }
        )
        var writtenMessage by remember { mutableStateOf("") }
        fun send() {
            val message = writtenMessage
            writtenMessage = ""
            scope.launch {
                channel.sendMessage(message)
                newMessage()
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {

            Scaffold(
                topBar = {
                    val showNewMessages by remember {
                        derivedStateOf {
                            if (!listState.canScrollForward) numNewMessages = 0
                            listState.canScrollForward && numNewMessages > 0
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(color = MaterialTheme.colorScheme.primary),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AnimatedVisibility(visible = dataStore.mobileUi) {
                            Button(
                                onClick = {
                                    dataStore.showGuilds = !dataStore.showGuilds
                                },
                                modifier = Modifier.align(alignment = Alignment.CenterVertically)
                            ) {
                                Text("Show Guilds")
                            }
                        }
                        Box(
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        ) {
                            Text(channel.name)
                        }
                        Button(
                            onClick = {
                                showMemberList = !showMemberList
                            },
                            modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.CenterVertically)
                        ) {
                            Text("Members")
                        }
                    }

                    if (showNewMessages) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(top = 48.dp)
                                .clickable {
                                    numNewMessages = 0
                                    scrollToBottom(true)
                                }
                                .background(color = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("$numNewMessages new messages")
                        }
                    }
                },
                bottomBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        TextField(
                            value = writtenMessage,
                            onValueChange = {
                                writtenMessage = it
                            },
                            singleLine = true,
                            modifier = Modifier.onKeyEvent {
                                if (it.key == Key.Enter) {
                                    send()
                                    true
                                } else {
                                    false
                                }
                            }

                        )
                        Button(onClick = {
                            send()
                        }) {
                            Text("Send")
                        }
                    }
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 48.dp
                        ),
                    state = listState,
                ) {

                    items(
                        items = channel.messages,
                        key = { it.id }
                    ) {
                        message(it)
                    }
                }
            }
        }
        AnimatedVisibility(visible = showMemberList) {
            MemberList(channel)
        }
    }

}