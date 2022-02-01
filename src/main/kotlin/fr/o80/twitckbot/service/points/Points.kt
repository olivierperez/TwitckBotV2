package fr.o80.twitckbot.service.points

interface Points {
    suspend fun getPoints(login: String): Int
    suspend fun addPoints(login: String, points: Int)
    suspend fun consumePoints(login: String, points: Int): Boolean
}