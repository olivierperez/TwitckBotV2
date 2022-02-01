package fr.o80.twitckbot.service.storage

import java.io.File

interface Storage {
    suspend fun hasUserInfo(login: String): Boolean
    suspend fun putUserInfo(login: String, namespace: String, key: String, value: String)
    suspend fun getUserInfo(login: String, namespace: String, key: String): String?

    suspend fun putGlobalInfo(namespace: String, key: String, value: String)
    suspend fun getGlobalInfo(namespace: String): List<Pair<String, String>>
    //fun getGlobalInfo(namespace: String, key: String): String?

    fun getPathOf(path: String, file: String): File
}