package fr.o80.twitckbot.service.points

interface Points {
    fun getPoints(login: String): Int
    fun addPoints(login: String, points: Int)
    fun consumePoints(login: String, points: Int): Boolean
}