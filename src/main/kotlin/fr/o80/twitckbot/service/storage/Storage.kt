package fr.o80.twitckbot.service.storage

import java.io.File

interface Storage {
    fun hasUserInfo(login: String): Boolean
    fun putUserInfo(login: String, namespace: String, key: String, value: String)
    fun getUserInfo(login: String, namespace: String, key: String): String?

    fun putGlobalInfo(namespace: String, key: String, value: String)
    fun getGlobalInfo(namespace: String): List<Pair<String, String>>
    //fun getGlobalInfo(namespace: String, key: String): String?

    fun getPathOf(path: String, file: String): File
}