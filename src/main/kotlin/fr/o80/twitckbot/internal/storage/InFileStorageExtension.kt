package fr.o80.twitckbot.internal.storage

import fr.o80.twitckbot.internal.storage.bean.Global
import fr.o80.twitckbot.internal.storage.bean.User
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.storage.Storage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

private val storageSerializer = Json {
    prettyPrint = true
}

private inline fun <reified T : Any> String.parse(): T {
    return storageSerializer.decodeFromString<T>(this)
}

class InFileStorageExtension @Inject constructor(
    loggerFactory: LoggerFactory,
    private val sanitizer: FileNameSanitizer
) : Storage {

    private val logger = loggerFactory.getLogger(InFileStorageExtension::class)

    // TODO OPZ Inject the Storage directory ?
    private val outputDirectory = File(".storage/")
    private val usersDirectory = File(outputDirectory, "users")

    private val lock = Mutex()

    init {
        if (!outputDirectory.exists())
            outputDirectory.mkdirs()
        if (!usersDirectory.exists())
            usersDirectory.mkdirs()
        if (!outputDirectory.isDirectory) {
            throw IllegalStateException("The path ${outputDirectory.absolutePath} is not a directory!")
        }
    }

    override suspend fun hasUserInfo(login: String): Boolean {
        lock.withLock {
            logger.trace("Check if user info exists $login")
            val userFile = getUserFile(login)
            return userFile.isFile
        }
    }

    override suspend fun putUserInfo(login: String, namespace: String, key: String, value: String) {
        lock.withLock {
            logger.trace("Putting user info into $login [$namespace//$key] => $value")
            with(getOrCreateUser(login)) {
                putExtra("$namespace//$key", value)
                save(this)
            }
            logger.trace("END putUserInfo || ${Thread.currentThread().name}")
        }
    }

    override suspend fun getUserInfo(login: String, namespace: String, key: String): String? =
        lock.withLock {
            logger.trace("Getting user info of $login [$namespace//$key]")
            return getOrCreateUser(login).getExtra("$namespace//$key")
        }

    override suspend fun putGlobalInfo(namespace: String, key: String, value: String) {
        lock.withLock {
            logger.trace("Putting info into [$namespace//$key] => $value")
            with(getOrCreateGlobal()) {
                putExtra(namespace, key, value)
                save(this)
            }
        }
    }

    override suspend fun getGlobalInfo(namespace: String): List<Pair<String, String>> =
        lock.withLock {
            logger.trace("Getting all info [$namespace]")
            return getOrCreateGlobal().getExtras(namespace)
        }

    override suspend fun getGlobalInfo(namespace: String, key: String): String? =
        lock.withLock {
            logger.trace("Getting info [$namespace//$key]")
            return getOrCreateGlobal().getExtras(namespace).firstOrNull { it.first == key}?.second
        }

    override fun getPathOf(path: String, file: String): File {
        return File(outputDirectory, "$path/$file")
    }

    private fun getOrCreateGlobal(): Global {
        val globalFile = getGlobalFile()
        return if (globalFile.isFile) {
            globalFile.reader().use { reader ->
                reader.readText().parse()
            }
        } else {
            logger.trace("There's no global file")
            Global()
        }
    }

    private fun save(global: Global) {
        logger.trace("Saving global...")
        try {
            val globalFile = getGlobalFile()
            val globalJson = storageSerializer.encodeToString(global)
            globalFile.writer().use {
                it.write(globalJson)
            }
            logger.trace("Global saved")
        } catch (e: Exception) {
            logger.error("Failed to save", e)
        }
    }

    private fun getOrCreateUser(login: String): User {
        val userFile = getUserFile(login)
        return if (userFile.isFile) {
            logger.trace("User $login already has a file !")
            userFile.reader().use { reader ->
                reader.readText().parse()
            }
        } else {
            logger.trace("User $login has no files")
            User(login)
        }
    }

    private fun save(user: User) {
        logger.debug("Saving user ${user.login}...")
        try {
            val userFile = getUserFile(user.login)
            val userJson = storageSerializer.encodeToString(user)
            logger.trace("About to store into ${user.login}\n-----\n$userJson\n-----")
            userFile.writer().use {
                it.write(userJson)
            }
            logger.trace("User ${user.login} saved")
        } catch (e: Exception) {
            logger.error("Failed to save", e)
        }
    }

    private fun getGlobalFile() =
        File(outputDirectory, "global.json")

    private fun getUserFile(login: String) =
        File(usersDirectory, "${sanitizer(login)}.json")
}
