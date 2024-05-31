package uninit.ui.theme

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.*
import uninit.common.collections.Three
import uninit.common.collections.Two
import uninit.ui.theme.catppuccin.CatppuccinMochaPink

/**
 * Loosely based off of the Catppucin Style Guide
 *
 * Easily serializable, allowing for easy custom themes.
 */
@Serializable(ApplicationTheme.Serializer::class)
data class ApplicationTheme(
    val id: String,
    val backgroundPane: Color,
    val secondaryPanes: Two<Color>,
    val surfaceElements: Three<Color>,
    val overlays: Three<Color>,
    val body: Color,
    val header: Color,
    val subheaders: Two<Color>,
    val accent: Color,
    
    val success: Color?,
    val warning: Color?,
    val error: Color?,
    val info: Color?,
    
    val selectionOverlay: Color = overlays[1].atOpacity(0.2f),
    
    val cursor: Color?,
) {
    inner class Serializer : KSerializer<ApplicationTheme> {
        private val colorDescriptor = String.serializer().descriptor
        private val nullableColorDescriptor = colorDescriptor.nullable
        @OptIn(ExperimentalSerializationApi::class)
        val colorArrayDescriptor = buildClassSerialDescriptor("Array<${colorDescriptor.serialName}>")
        override val descriptor = buildClassSerialDescriptor("ApplicationTheme") {
            element("id", String.serializer().descriptor) // this is equivalent but not using the color descriptor is symbolic
            element("backgroundPane", colorDescriptor)
            element("secondaryPanes", colorArrayDescriptor)
            element("surfaceElements", colorArrayDescriptor)
            element("overlays", colorArrayDescriptor)
            element("body", colorDescriptor)
            element("header", colorDescriptor)
            element("subheaders", colorArrayDescriptor)
            element("accent", colorDescriptor)

            element("success", nullableColorDescriptor)
            element("warning", nullableColorDescriptor)
            element("error", nullableColorDescriptor)
            element("info", nullableColorDescriptor)

            element("selectionOverlay", colorDescriptor)

            element("cursor", nullableColorDescriptor)
        }



        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): ApplicationTheme {
            val listDeserializer = ListSerializer(String.serializer())
            val nullableStringDeserializer = String.serializer().nullable
            decoder.decodeStructure(descriptor) {
                var id: String? = null
                var backgroundPane: Color? = null
                var secondaryPanes: List<Color>? = null
                var surfaceElements: List<Color>? = null
                var overlays: List<Color>? = null
                var body: Color? = null
                var header: Color? = null
                var subheaders: List<Color>? = null
                var accent: Color? = null

                var success: Color? = null
                var warning: Color? = null
                var error: Color? = null
                var info: Color? = null

                var selectionOverlay: Color? = null

                var cursor: Color? = null

                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeStringElement(descriptor, index)
                        1 -> backgroundPane = Color.fromHex(decodeStringElement(descriptor, index))
                        2 -> secondaryPanes = decodeSerializableElement(
                            colorArrayDescriptor,
                            index,
                            listDeserializer
                        ).map { Color.fromHex(it) }

                        3 -> surfaceElements = decodeSerializableElement(
                            colorArrayDescriptor,
                            index,
                            listDeserializer
                        ).map { Color.fromHex(it) }

                        4 -> overlays = decodeSerializableElement(
                            colorArrayDescriptor,
                            index,
                            listDeserializer
                        ).map { Color.fromHex(it) }

                        5 -> body = Color.fromHex(decodeStringElement(descriptor, index))
                        6 -> header = Color.fromHex(decodeStringElement(descriptor, index))
                        7 -> subheaders = decodeSerializableElement(
                            colorArrayDescriptor,
                            index,
                            listDeserializer
                        ).map { Color.fromHex(it) }

                        8 -> accent = Color.fromHex(decodeStringElement(descriptor, index))

                        9 -> success = decodeNullableSerializableElement(
                            nullableColorDescriptor,
                            index,
                            nullableStringDeserializer
                        )?.let { Color.fromHex(it) }

                        10 -> warning = decodeNullableSerializableElement(
                            nullableColorDescriptor,
                            index,
                            nullableStringDeserializer
                        )?.let { Color.fromHex(it) }

                        11 -> error = decodeNullableSerializableElement(
                            nullableColorDescriptor,
                            index,
                            nullableStringDeserializer
                        )?.let { Color.fromHex(it) }

                        12 -> info = decodeNullableSerializableElement(
                            nullableColorDescriptor,
                            index,
                            nullableStringDeserializer
                        )?.let { Color.fromHex(it) }

                        13 -> selectionOverlay = Color.fromHex(decodeStringElement(descriptor, index))

                        14 -> cursor = decodeNullableSerializableElement(
                            nullableColorDescriptor,
                            index,
                            nullableStringDeserializer
                        )?.let { Color.fromHex(it) }

                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                fun List<Color>.asTypedArray() = this.toTypedArray()
                return@decodeStructure ApplicationTheme(
                    id = id!!,
                    backgroundPane = backgroundPane!!,
                    secondaryPanes = Two(secondaryPanes!!.asTypedArray()),
                    surfaceElements = Three(surfaceElements!!.asTypedArray()),
                    overlays = Three(overlays!!.asTypedArray()),
                    body = body!!,
                    header = header!!,
                    subheaders = Two(subheaders!!.asTypedArray()),
                    accent = accent!!,
                    success = success,
                    warning = warning,
                    error = error,
                    info = info,
                    selectionOverlay = selectionOverlay!!,
                    cursor = cursor,
                )
            }
            throw IllegalStateException("Should not reach here")
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: ApplicationTheme) {
            val listSerializer = ListSerializer(String.serializer())
            val nullableStringSerializer = String.serializer().nullable
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(String.serializer().descriptor, 0, String.serializer(), value.id)
                encodeStringElement(colorDescriptor, 1, value.backgroundPane.asHex)
                encodeSerializableElement(colorArrayDescriptor, 2, listSerializer, value.secondaryPanes.map { it.asHex })
                encodeSerializableElement(colorArrayDescriptor, 3, listSerializer, value.surfaceElements.map { it.asHex })
                encodeSerializableElement(colorArrayDescriptor, 4, listSerializer, value.overlays.map { it.asHex })
                encodeStringElement(colorDescriptor, 5, value.body.asHex)
                encodeStringElement(colorDescriptor, 6, value.header.asHex)
                encodeSerializableElement(colorArrayDescriptor, 7, listSerializer, value.subheaders.map { it.asHex })
                encodeStringElement(colorDescriptor, 8, value.accent.asHex)

                encodeNullableSerializableElement(nullableColorDescriptor, 9, nullableStringSerializer, value.success?.asHex)
                encodeNullableSerializableElement(nullableColorDescriptor, 10, nullableStringSerializer, value.warning?.asHex)
                encodeNullableSerializableElement(nullableColorDescriptor, 11, nullableStringSerializer, value.error?.asHex)
                encodeNullableSerializableElement(nullableColorDescriptor, 12, nullableStringSerializer, value.info?.asHex)

                encodeStringElement(colorDescriptor, 13, value.selectionOverlay.asHex)

                encodeNullableSerializableElement(nullableColorDescriptor, 14, nullableStringSerializer, value.cursor?.asHex)
            }
        }
    }

    companion object {
        val default = CatppuccinMochaPink
    }
}

