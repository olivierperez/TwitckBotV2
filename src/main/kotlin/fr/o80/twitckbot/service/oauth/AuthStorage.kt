package fr.o80.twitckbot.service.oauth

import fr.o80.twitckbot.data.model.FullAuth

interface AuthStorage {
    fun store(fullAuth: FullAuth)
    fun readAuth(): FullAuth?
}
