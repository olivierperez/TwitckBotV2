package fr.o80.twitckbot.service.connectable

import fr.o80.twitckbot.service.log.Logger
import kotlinx.coroutines.delay
import kotlin.math.pow

class Retrier(
    private val logger: Logger,
    private val message: String,
    private val successCondition: () -> Boolean
) {
    suspend fun start(onSuccess: () -> Unit, onFail: () -> Unit?) {
        var retryDepth = 0
        while (!successCondition() && retryDepth < 3) {
            logger.info(message)
            delay(200 * 2.0.pow(retryDepth).toLong())
            retryDepth++
        }

        if (successCondition()) {
            onSuccess()
        } else {
            onFail()
        }
    }
}
