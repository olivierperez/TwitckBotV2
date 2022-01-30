package fr.o80.twitckbot.system.json

import fr.o80.twitckbot.utils.addPrefix
import fr.o80.twitckbot.system.bean.ChannelName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object ChannelNameSerializer : KSerializer<ChannelName> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ChannelName", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ChannelName {
        return ChannelName(decoder.decodeString().addPrefix("#"))
    }

    override fun serialize(encoder: Encoder, value: ChannelName) {
        encoder.encodeString(value.name)
    }
}
