package uninit.common.serialization

import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 * "Ported" from [mewsic-common](https://github.com/mewsicapp/common/blob/meow/src/commonMain/kotlin/com/mewsic/common/processing/JsonParser.kt)
 * This represents a parsed JSON object with no type information.
 */
class JsonParser {
    @PublishedApi
    internal val deserializer = Json {
        ignoreUnknownKeys = true
    }
    val element: JsonElement

    /**
     * @constructor Creates a new JsonParser object from a [String].
     * @param json The [String] to parse.
     */

    constructor(json: String) {
        element = deserializer.parseToJsonElement(json)
    }

    /**
     * @constructor Creates a new JsonParser object from a [JsonElement].
     * @param element the [JsonElement] this parser represents.
     */

    constructor(json: JsonElement) {
        element = json
    }

    /**
     * Get the value of the given index.
     * @param index The index of the value to get.
     * @return The value of the given index.
     * @throws IllegalArgumentException If the element is not a [JsonArray].
     * @operator []
     */
    operator fun get(index: Int) : JsonParser? {
        if (element !is JsonArray) {
            throw IllegalArgumentException("Element is not a JsonArray")
        }
        if (element.jsonArray.size <= index) {
            return null
        }
        return JsonParser(element.jsonArray[index])
    }
    /**
     * Get the value of the given key.
     * @param key The key of the value to get.
     * @return The value of the given key.
     * @throws IllegalArgumentException If the element is not a [JsonObject].
     * @operator []
     */
    operator fun get(key: String) : JsonParser? {
        return element.jsonObject[key]?.let { JsonParser(it) }
    }

    /**
     * Check if the key is present in the element.
     * @param key The key to check.
     * @return True if the key is present, false otherwise.
     * @throws IllegalArgumentException If the element is not a [JsonObject].
     */
    fun has(key: String) : Boolean {
        return element.jsonObject.containsKey(key)
    }

    /**
     * Interprets the element as any deserializable type.
     * @param T The type to interpret the element as.
     * @return The interpreted element.
     * @throws SerializationException if the object cannot be decoded into T.
     *
     */
    inline fun <reified T> interpret() : T {
        return deserializer.decodeFromJsonElement(element)
    }

    /**
     * Interprets the element at the given key as any deserializable type.
     * @param key The key of the element to interpret.
     * @param T The type to interpret the element as.
     * @return The interpreted element.
     * @throws SerializationException if the object cannot be decoded into T.
     */
    inline fun <reified T> get(key: String) : T {
        return deserializer.decodeFromJsonElement(element.jsonObject[key]!!)
    }

    /**
     * Takes in a string in the format of "key1.key2[3].key4" and returns a [JsonParser] that points to the value of the given key.
     * @param path The path to the value to get.
     * @return A [JsonParser] that points to the value of the given key.
     * @throws IllegalArgumentException If the element that the path points to is not of valid type for the next step.
     */
    fun travel(path: String): JsonParser {
        var current = this
        for (key in path.split(".", "[", "]")) {
            current = try {
                current[key.toInt()]!!
            } catch (e: NumberFormatException) {
                current[key]!!
            }
        }
        return current
    }

    /**
     * Turns the element into a [List<JsonParser>]
     * @return A [List<JsonParser>] that contains all the elements in the element.
     * @throws IllegalArgumentException If the element is not a [JsonArray].
     */
    fun toList() : List<JsonParser> {
        return element.jsonArray.map { JsonParser(it) }
    }

    /**
     * @return The element as a [String].
     */
    override fun toString(): String {
        return element.toString()
    }

    /**
     * The value of the element parsed as a [String].
     */
    val string: String
        get() = interpret()
    /**
     * The value of the element parsed as an [Int].
     */
    val int: Int
        get() = interpret()
    /**
     * The value of the element parsed as a [Long].
     */
    val long: Long
        get() = interpret()
    /**
     * The value of the element parsed as a [Double].
     */
    val double: Double
        get() = interpret()
    /**
     * The value of the element parsed as a [Boolean].
     */
    val bool: Boolean
        get() = interpret()
}