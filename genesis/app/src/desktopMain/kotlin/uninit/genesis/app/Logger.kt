package uninit.genesis.app


import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.Handler
import java.util.regex.Pattern

private class AntiLog(
    private val defaultTag: String = "app",
    private val handler: List<Handler> = listOf()
) : Antilog() {
    constructor(defaultTag: String) : this(defaultTag, handler = listOf())

    companion object {
        private const val CALL_STACK_INDEX = 8
    }


    private val anonymousClass = Pattern.compile("(\\$\\d+)+$")

    private val tagMap: HashMap<LogLevel, String> = hashMapOf(
        LogLevel.VERBOSE to "[VERBOSE]",
        LogLevel.DEBUG to "[DEBUG]",
        LogLevel.INFO to "[INFO]",
        LogLevel.WARNING to "[WARN]",
        LogLevel.ERROR to "[ERROR]",
        LogLevel.ASSERT to "[ASSERT]"
    )

    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?,
    ) {

        val debugTag = tag ?: performTag(defaultTag)

        val fullMessage = if (message != null) {
            if (throwable != null) {
                "$message\n${throwable.stackTraceString}"
            } else {
                message
            }
        } else throwable?.stackTraceString ?: return

        when (priority) {
            LogLevel.VERBOSE -> println(buildLog(priority, debugTag, fullMessage))
            LogLevel.DEBUG -> println(buildLog(priority, debugTag, fullMessage))
            LogLevel.INFO -> println(buildLog(priority, debugTag, fullMessage))
            LogLevel.WARNING -> println(buildLog(priority, debugTag, fullMessage))
            LogLevel.ERROR -> System.err.println(buildLog(priority, debugTag, fullMessage))
            LogLevel.ASSERT -> System.err.println(buildLog(priority, debugTag, fullMessage))
        }
    }

    internal fun buildLog(priority: LogLevel, tag: String?, message: String?): String {
        return "${tagMap[priority]} ${tag ?: performTag(defaultTag)} - $message"
    }

    private fun performTag(defaultTag: String): String {
        val thread = Thread.currentThread().stackTrace

        return if (thread.size >= CALL_STACK_INDEX) {
            thread[CALL_STACK_INDEX].run {
                "${createStackElementTag(className)}\$$methodName"
            }
        } else {
            defaultTag
        }
    }

    internal fun createStackElementTag(className: String): String {
        var tag = className
        val m = anonymousClass.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag.substring(tag.lastIndexOf('.') + 1)
    }

    private val Throwable.stackTraceString
        get(): String {
            // DO NOT replace this with Log.getStackTraceString() - it hides UnknownHostException, which is
            // not what we want.
            val sw = StringWriter(256)
            val pw = PrintWriter(sw, false)
            printStackTrace(pw)
            pw.flush()
            return sw.toString()
        }
}

actual fun getAntiLog(): Antilog = AntiLog()