package fr.o80.twitckbot.extensions.overlay.graphics

interface Layer {
    fun init()
    fun tick()
    fun render()
}
