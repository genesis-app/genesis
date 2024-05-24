package uninit.genesis.discord.client.gateway.types

import kotlinx.serialization.Serializable

@Serializable
data class SuperProperties(
    val browser: String = "Discord Android",
    val browser_user_agent: String = "",
    val browser_version: String = "",
    val client_build_number: Int = 200207,
//    val client_event_source: null,
    val client_version: String = "200.7 - rn",
    val design_id: Int = 0,
    val device: String = "windows_x86_64",
    val device_vendor_id: String = "5eb95c36-450e-4535-a0db-c42610b09096",
    val os: String = "Android",
    val os_version: String = "33",
    val release_channel: String = "canaryRelease",
    val system_locale: String = "en-US",
)
