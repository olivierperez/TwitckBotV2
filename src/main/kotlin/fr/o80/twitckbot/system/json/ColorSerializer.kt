package fr.o80.twitckbot.system.json

import fr.o80.twitckbot.system.bean.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object ColorSerializer : KSerializer<Color> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ChannelName", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Color {
        val rgb = decoder.decodeString().removePrefix("#")
        val (red, green, blue) = rgb.chunked(2)

        return Color(
            red = Integer.parseInt(red,16),
            green = Integer.parseInt(green,16),
            blue = Integer.parseInt(blue,16),
        )
    }

    override fun serialize(encoder: Encoder, value: Color) {
        val redHex = value.red.toString(16)
        val greenHex = value.green.toString(16)
        val blueHex = value.blue.toString(16)
        encoder.encodeString("#$redHex$greenHex$blueHex")
    }
}
