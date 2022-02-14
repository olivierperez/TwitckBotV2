package fr.o80.twitckbot.system.json

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DateSerializerTest {

    private val deserializer = DateSerializer

    @Test
    fun shouldParseWithSeconds() {
        // Given
        val dateWithMillis = "2022-02-14T07:45:56Z"
        val expectedDate = LocalDateTime.of(2022, 2, 14, 7, 45, 56)

        // When
        val date = deserializer.parse(dateWithMillis)

        // Then
        assertEquals(expectedDate, date)
    }

    @Test
    fun shouldFormatWithSeconds() {
        // Given
        val date = LocalDateTime.of(2022, 2, 14, 7, 45, 56)
        val dateWithMillis = "2022-02-14T07:45:56Z"

        // When
        val dateString = deserializer.format(date)

        // Then
        assertEquals(dateWithMillis, dateString)
    }
}
