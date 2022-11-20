package fr.o80.twitckbot.internal.time

import fr.o80.twitckbot.service.connectable.chat.CoolDown
import java.time.LocalDateTime
import javax.inject.Inject

class CoolDownManager @Inject constructor() {

    private val coolDowns: MutableMap<String, LocalDateTime> = mutableMapOf()

    fun executeIfCooledDown(
        namespace: String,
        key: String,
        coolDown: CoolDown?,
        block: () -> Unit
    ) {
        if (hasCooledDown(namespace, key)) {
            startCoolDown(namespace, key, coolDown)
            block()
        }
    }

    private fun hasCooledDown(namespace: String, key: String): Boolean {
        val expiry = coolDowns["$namespace::$key"]
        return expiry == null || LocalDateTime.now().isAfter(expiry)
    }

    private fun startCoolDown(namespace: String, key: String, coolDown: CoolDown?) {
        coolDown?.duration?.let { duration ->
            coolDowns["$namespace::$key"] = LocalDateTime.now() + duration
        }
    }

}
