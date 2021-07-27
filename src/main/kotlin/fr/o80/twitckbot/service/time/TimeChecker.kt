package fr.o80.twitckbot.service.time

interface TimeChecker {
    suspend fun executeIfNotCooldown(login: String, block: suspend () -> Unit): TimeFallback
}

interface TimeFallback {
    fun fallback(block: () -> Unit)
}

internal object NoOpFallback : TimeFallback {
    override fun fallback(block: () -> Unit) {
    }
}

internal object DoFallback : TimeFallback {
    override fun fallback(block: () -> Unit) {
        block()
    }
}
