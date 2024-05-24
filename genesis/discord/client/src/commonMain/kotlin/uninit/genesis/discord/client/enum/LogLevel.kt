package uninit.genesis.discord.client.enum

enum class LogLevel(val level: Int, val str: String) {
    NONE(0, "NONE"),
    ERROR(1, "ERROR"),
    WARN(2, "WARN"),
    INFO(3, "INFO"),
    VERBOSE(4, "VERBOSE"),
    DEBUG(5, "DEBUG"),
    TRACE(6, "TRACE"),
    NETWORK(7, "NETWORK");

    companion object {
        fun fromValue(level: Int): LogLevel = values().first { it.level == level }
        fun fromName(name: String): LogLevel = values().first { it.str == name }
    }
}