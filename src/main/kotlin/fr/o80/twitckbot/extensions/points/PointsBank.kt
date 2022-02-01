package fr.o80.twitckbot.extensions.points

import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.utils.tryToInt
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class PointsBank @Inject constructor(
    private val storage: Storage
) {

    private val namespace: String = PointsExtension::class.java.name

    private val lock = Mutex()

    suspend fun getPoints(login: String): Int =
        storage.getPoints(login)

    suspend fun addPoints(login: String, points: Int) {
        lock.withLock {
            val currentPoints = storage.getPoints(login)
            val newPoints = currentPoints + points
            storage.putPoints(login, newPoints)
        }
    }

    suspend fun removePoints(login: String, points: Int): Boolean =
        lock.withLock {
            if (canConsume(login, points)) {
                val currentPoints = storage.getPoints(login)
                val newPoints = currentPoints - points
                storage.putPoints(login, newPoints)
                true
            } else {
                false
            }
        }

    suspend fun transferPoints(fromLogin: String, toLogin: String, points: Int): Boolean =
        lock.withLock {
            if (canConsume(fromLogin, points)) {
                removePoints(fromLogin, points)
                addPoints(toLogin, points)
                true
            } else {
                false
            }
        }

    private suspend fun canConsume(login: String, points: Int): Boolean =
        storage.getPoints(login) >= points

    private suspend fun Storage.getPoints(login: String) =
        getUserInfo(login, namespace, "balance").tryToInt() ?: 0

    private suspend fun Storage.putPoints(login: String, points: Int) =
        putUserInfo(login, namespace, "balance", points.toString())
}
