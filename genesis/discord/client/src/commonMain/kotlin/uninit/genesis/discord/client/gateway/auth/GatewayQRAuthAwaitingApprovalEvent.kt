package uninit.genesis.discord.client.gateway.auth

data class GatewayQRAuthAwaitingApprovalEvent(
    val userName: String,
    val userId: String,
    val userAvatarUri: String,
)