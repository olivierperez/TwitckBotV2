package fr.o80.twitckbot.internal.storage

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FileNameSanitizerTest {

    private val sanitizer = FileNameSanitizer()

    @Test
    fun `Should sanitize a messy file name`() {
        val input = "Filename-_4875 è-ds"
        val output = sanitizer(input)
        assertEquals("Filename_4875_ds", output)
    }
}
