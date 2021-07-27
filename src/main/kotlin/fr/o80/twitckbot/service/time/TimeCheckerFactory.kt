package fr.o80.twitckbot.service.time

import fr.o80.twitckbot.service.storage.Storage
import java.time.Duration
import javax.inject.Inject
import kotlin.reflect.KClass

class TimeCheckerFactory @Inject constructor(
    private val storage: Storage
) {
    fun create(
        namespace: KClass<out Any>,
        flag: String,
        interval: Duration,
    ): TimeChecker = StorageFlagTimeChecker(
        storage = storage,
        flag = flag,
        interval = interval,
        namespace = namespace.java.name
    )
}
