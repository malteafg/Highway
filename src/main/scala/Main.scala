import java.nio.DoubleBuffer

import input.InputHandler
import input.InputHandler.{keyPressed, mousePressed}
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.{GLFWKeyCallback, GLFWVidMode}
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.{GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT, GL_DEPTH_TEST, GL_VERSION, glClear, glClearColor, glEnable, glGetString}
import org.lwjgl.opengl.GL13.{GL_TEXTURE1, glActiveTexture}
import org.lwjgl.system.MemoryUtil.NULL

object Main {

    var running: Boolean = true
    var window: Long = -1

    private def init(): Unit = {
        // initting opengl
        if (glfwInit != true) {
            System.err.println("Could not initialize GLFW!")
            return
        }

        // creating window
        val monitor = glfwGetPrimaryMonitor
        val vidmode = glfwGetVideoMode(monitor)
        glfwWindowHint(GLFW_RED_BITS, vidmode.redBits)
        glfwWindowHint(GLFW_GREEN_BITS, vidmode.greenBits)
        glfwWindowHint(GLFW_BLUE_BITS, vidmode.blueBits)
        glfwWindowHint(GLFW_REFRESH_RATE, vidmode.refreshRate)
        window = glfwCreateWindow(Vars.WIDTH.toInt, Vars.HEIGHT.toInt, "Highway Architect", NULL, NULL)
        if (window == NULL) {
            System.err.println("Could not create GLFW window!")
            return
        }
        glfwSetWindowPos(window, vidmode.width / 2 - Vars.WIDTH.toInt / 2, vidmode.height / 2 - Vars.HEIGHT.toInt / 2)

        glfwSetKeyCallback(window, InputHandler.keyPressed)
        glfwSetMouseButtonCallback(window, InputHandler.mousePressed)
        glfwSetCursorPosCallback(window, InputHandler.mouseMoved)

        glfwMakeContextCurrent(window)
        glfwShowWindow(window)

        GL.createCapabilities

        // setting graphics
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glEnable(GL_DEPTH_TEST)
        glActiveTexture(GL_TEXTURE1)
        System.out.println("OpenGL: " + glGetString(GL_VERSION))
    }

    private def gameUpdate(): Unit = {
        glfwPollEvents()
    }

    private def gameRender(): Unit = {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
        glfwSwapBuffers(window)
    }

    def main(args: Array[String]): Unit = {
        val thread = new Thread {
            override def run(): Unit = {
                init

                var lastTime = System.nanoTime
                var timer = System.currentTimeMillis
                val ns = 1000000000.0 / 60.0
                var delta = 0.0
                var u = 0
                var f = 0
                while (running) {
                    val now = System.nanoTime
                    delta += (now - lastTime) / ns
                    lastTime = now
                    while (delta >= 1) {
                        gameUpdate
                        u += 1
                        delta -= 1
                    }
                    gameRender
                    f += 1
                    if (System.currentTimeMillis - timer > 1000) {
                        timer += 1000
                        System.out.println("UPS: " + u + "  FPS: " + f)
                        u = 0
                        f = 0
                    }
                    if (glfwWindowShouldClose(window)) running = false
                }
            }
        }
        thread.start()
    }

}
