package fr.o80.twitckbot.extensions.overlay.graphics

import fr.o80.twitckbot.extensions.overlay.graphics.ext.Vertex3f
import fr.o80.twitckbot.extensions.overlay.graphics.ext.draw
import fr.o80.twitckbot.service.log.Logger
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL46
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

class OverlayWindow(
    private val title: String,
    private val width: Int,
    private val height: Int,
    private val bgColor: Vertex3f,
    private val updatesPerSecond: Int,
    private val logger: Logger
) : Runnable {

    private var window: Long = 0

    private val renderersToInit: Queue<Layer> = ConcurrentLinkedDeque()
    private val layers: MutableList<Layer> = mutableListOf()

    private val keyPipeline: KeyPipeline = KeyPipelineImpl()

    override fun run() {
        configure()
        loop()
    }

    private fun configure() {
        logger.debug("Overlay configured on ${Thread.currentThread().name}")
        GLFWErrorCallback.createPrint(System.err).set()

        if (!GLFW.glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE)

        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (window == MemoryUtil.NULL) {
            throw IllegalStateException("Failed to create window")
        }

        keyPipeline.onKey(GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_RELEASE) {
            GLFW.glfwSetWindowShouldClose(window, true)
        }

        GLFW.glfwSetKeyCallback(window, keyPipeline)
        /*GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback)
        GLFW.glfwSetCursorPosCallback(window, mouseMoveCallback)*/

        MemoryStack.stackPush().use { stack ->
            val widthBuffer = stack.mallocInt(1)
            val heightBuffer = stack.mallocInt(1)

            GLFW.glfwGetWindowSize(window, widthBuffer, heightBuffer)

            val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())!!

            GLFW.glfwSetWindowPos(
                window,
                (videoMode.width() - widthBuffer.get(0)) / 2,
                (videoMode.height() - heightBuffer.get(0)) / 2
            )
        }

        GLFW.glfwMakeContextCurrent(window)
        GLFW.glfwSwapInterval(1)

        GLFW.glfwShowWindow(window)

        GL.createCapabilities()

        GL46.glMatrixMode(GL46.GL_PROJECTION)
        GL46.glOrtho(0.0, width.toDouble(), height.toDouble(), 0.0, 0.0, 1.0)
    }

    private fun loop() {
        var lastTime = GLFW.glfwGetTime()
        var timer = lastTime
        var delta = 0.0
        var now: Double
        var frames = 0
        var updates = 0
        val limitFPS = 1f / updatesPerSecond

        while (!GLFW.glfwWindowShouldClose(window)) {

            now = GLFW.glfwGetTime()
            delta += (now - lastTime) / limitFPS

            lastTime = now

            while (delta > 1.0) {
                tickRenderers()
                updates++
                delta--
            }

            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT or GL46.GL_DEPTH_BUFFER_BIT)
            initRenderers()
            executeRenders()
            GLFW.glfwPollEvents()
            GLFW.glfwSwapBuffers(window)
            frames++

            if (GLFW.glfwGetTime() - timer > 1) {
                timer++
                logger.trace("FPS: $frames Updates: $updates")
                logger.trace("on ${Thread.currentThread().name}")
                frames = 0
                updates = 0
            }
        }

        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)

        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    private fun tickRenderers() {
        layers.forEach { it.tick() }
    }

    private fun initRenderers() {
        var elem = renderersToInit.poll()
        while (elem != null) {
            elem.init()
            elem = renderersToInit.poll()
        }
    }

    private fun executeRenders() {
        draw {
            clear(bgColor.x, bgColor.y, bgColor.z)
        }
        layers.forEach { it.render() }
    }

    fun registerRender(layer: Layer) {
        layers.add(layer)
        renderersToInit.offer(layer)
    }

}