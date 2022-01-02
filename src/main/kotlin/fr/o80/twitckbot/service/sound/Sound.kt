package fr.o80.twitckbot.service.sound

interface Sound {
    fun play(id: String)
    fun playCelebration()
    fun playNegative()
    fun playPositive()
    fun playRaid()
}
