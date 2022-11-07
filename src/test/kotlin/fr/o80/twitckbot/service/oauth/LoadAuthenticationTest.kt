package fr.o80.twitckbot.service.oauth

import fr.o80.twitckbot.data.model.Auth
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.service.config.LoadConfig
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.time.Instant
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
@DisplayName("Load authentication use case")
internal class LoadAuthenticationTest {

    @InjectMockKs
    lateinit var loadConfig: LoadConfig

    @MockK
    lateinit var authStorage: AuthStorage

    @Test
    @DisplayName("Don't authenticate the user if there is no saves")
    fun noSavesNoAuthentication() {
        // Given
        every { authStorage.readAuth() } returns null

        // When
        val auth = loadConfig()

        // Then
        assertFalse(auth.isComplete())
    }

    @Test
    @DisplayName("Don't authenticate the user if the saved one is expired")
    fun noExpiredAuthenticationNoAuthentication() {
        // Given
        every { authStorage.readAuth() } returns FullAuth(
            botAuth = Auth(
                tokenType = "azerty",
                accessToken = "azerty",
                expiresAt = Instant.now() - Duration.ofSeconds(5),
                scopes = listOf()
            ),
            broadcasterAuth = Auth(
                tokenType = "azerty",
                accessToken = "azerty",
                expiresAt = Instant.now() - Duration.ofSeconds(5),
                scopes = listOf()
            )
        )

        // When
        val config = loadConfig()

        // Then
        assertFalse(config.isComplete())
    }

    @Test
    @DisplayName("Authenticate the user if the saved one is up to date")
    fun loadSavedAuthentication() {
        // Given
        every { authStorage.readAuth() } returns FullAuth(
            botAuth = Auth(
                tokenType = "azerty",
                accessToken = "azerty",
                expiresAt = Instant.now() + Duration.ofDays(30),
                scopes = listOf()
            ),
            broadcasterAuth = Auth(
                tokenType = "azerty",
                accessToken = "azerty",
                expiresAt = Instant.now() + Duration.ofDays(30),
                scopes = listOf()
            )
        )

        // When
        val auth = loadConfig()

        // Then
        assertNotNull(auth)
    }
}