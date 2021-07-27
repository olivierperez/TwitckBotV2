package fr.o80.twitckbot.internal.storage.bean

import kotlinx.serialization.Serializable

@Serializable
data class Global(
    val namespaces: MutableMap<String, Extras> = mutableMapOf()
) {

    fun putExtra(namespace: String, key: String, value: String) {
        namespaces.compute(namespace) { _, extras ->
            (extras ?: Extras()).apply {
                put(key, value)
            }
        }
    }

    fun getExtras(namespace: String): List<Pair<String, String>> {
        return namespaces[namespace]?.getAll() ?: emptyList()
    }
}

@Serializable
class Extras(
    val extras: MutableMap<String, String> = mutableMapOf()
) {
    fun put(key: String, value: String) {
        extras[key] = value
    }

    fun getAll(): List<Pair<String, String>> {
        return extras.entries.map { it.toPair() }
    }
}
