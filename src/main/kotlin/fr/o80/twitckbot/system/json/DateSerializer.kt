package fr.o80.twitckbot.system.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

internal object DateSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    private val formatters = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"),
    )

    override fun serialize(encoder: Encoder, date: LocalDateTime) {
        val formattedDate = format(date)
        encoder.encodeString(formattedDate)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val dateString = decoder.decodeString()
        return parse(dateString)
    }

    fun format(date: LocalDateTime) = formatters.firstNotNullOf { formatter ->
        try {
            date.format(formatter)
        } catch(e: DateTimeParseException) {
            e.printStackTrace()
            null
        }
    }

    fun parse(dateString: String) = formatters.firstNotNullOf { formatter ->
        try {
            LocalDateTime.parse(dateString, formatter)
        } catch(e: DateTimeParseException) {
            e.printStackTrace()
            null
        }
    }
}
