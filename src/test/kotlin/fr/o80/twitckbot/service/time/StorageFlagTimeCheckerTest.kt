package fr.o80.twitckbot.service.time

import fr.o80.twitckbot.service.storage.Storage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StorageFlagTimeCheckerTest {

    private val storage = mockk<Storage>()

    @Test
    fun `User interaction should be skipped if last is too recent`() = runBlocking {
        val interval = Duration.ofMinutes(5)
        val now = LocalDateTime.of(2020, 9, 14, 16, 43, 13, 52)
        val tooRecent = "2020-09-14T16:40:00.0"
        coEvery { storage.getUserInfo(any(), any(), any()) } returns tooRecent

        val timeChecker = StorageFlagTimeChecker(
            storage = storage,
            namespace = "don't care",
            flag = "don't care the flag",
            interval = interval,
            now = { now }
        )

        val couldExecute = timeChecker.couldExecute("should be skipped")

        assertFalse(couldExecute)
        coVerify { storage.getUserInfo("should be skipped", "don't care", "don't care the flag") }
    }

    @Test
    fun `User interaction should be played if last very old`() = runBlocking {
        val interval = Duration.ofMinutes(30)
        val now = LocalDateTime.of(2020, 9, 14, 16, 43, 13, 52)
        val oldInteraction = "2020-09-14T16:13:00.0"
        coEvery { storage.getUserInfo(any(), any(), any()) } returns oldInteraction

        val timeChecker = StorageFlagTimeChecker(
            storage = storage,
            namespace = "don't care",
            flag = "don't care the flag",
            interval = interval,
            now = { now }
        )

        val couldExecute = timeChecker.couldExecute("should be played")

        assertTrue(couldExecute)
        coVerify { storage.getUserInfo("should be played", "don't care", "don't care the flag") }
    }

    @Test
    fun `User interaction should be played if it never happened`() = runBlocking {
        val interval = Duration.ofMinutes(30)
        val now = LocalDateTime.of(2020, 9, 14, 16, 43, 13, 52)
        val oldInteraction = null
        coEvery { storage.getUserInfo(any(), any(), any()) } returns oldInteraction

        val timeChecker = StorageFlagTimeChecker(
            storage = storage,
            namespace = "don't care",
            flag = "don't care the flag",
            interval = interval,
            now = { now }
        )

        val couldExecute = timeChecker.couldExecute("should be played")

        assertTrue(couldExecute)
        coVerify { storage.getUserInfo("should be played", "don't care", "don't care the flag") }
    }

    @Test
    fun `Storage should store the last date`() = runBlocking {
        coEvery { storage.putUserInfo(any(), any(), any(), any()) } returns Unit

        val timeChecker = StorageFlagTimeChecker(
            storage = storage,
            namespace = "the namespace",
            flag = "the flag",
            interval = Duration.ofMinutes(5),
            now = { LocalDateTime.of(2020, 9, 15, 17, 23, 15, 0) }
        )

        timeChecker.handled("a login")

        coVerify {
            storage.putUserInfo(
                "a login",
                "the namespace",
                "the flag",
                "2020-09-15T17:23:15"
            )
        }
    }
}