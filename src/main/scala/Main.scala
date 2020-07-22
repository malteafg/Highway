import game.GameHandler
import org.lwjgl.opengl.GL11._
import input.InputHandler
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.{GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT, GL_DEPTH_TEST, GL_VERSION, glClear, glClearColor, glEnable, glGetString}
import org.lwjgl.opengl.GL13._
import org.lwjgl.system.MemoryUtil.NULL
import utils.{Options, Vals}
import ui.Interface
import utils.loader.{ShaderLoader, TextureLoader}

object Main {

    var running: Boolean = true
    var window: Long = -1

    private def init(): Unit = {
        // initting opengl
        if (!glfwInit) {
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
        glfwWindowHint(GLFW_SAMPLES, 4)
        window = glfwCreateWindow(Vals.WIDTH.toInt, Vals.HEIGHT.toInt, "Highway Architect", NULL, NULL)
        if (window == NULL) {
            System.err.println("Could not create GLFW window!")
            return
        }
        glfwSetWindowPos(window, vidmode.width / 2 - Vals.WIDTH.toInt / 2, vidmode.height / 2 - Vals.HEIGHT.toInt / 2)

        glfwSetKeyCallback(window, InputHandler.keyPressed)
        glfwSetCharCallback(window, InputHandler.charEntered)
        glfwSetMouseButtonCallback(window, InputHandler.mousePressed)
        glfwSetScrollCallback(window, InputHandler.mouseScrolled)
        glfwSetCursorPosCallback(window, InputHandler.mouseMoved)

        glfwMakeContextCurrent(window)
        glfwShowWindow(window)

        GL.createCapabilities

        // setting graphics
        glClearColor(3 / 255f, 199 / 255f, 247 / 255f, 1.0f)
        glEnable(GL_DEPTH_TEST)
        glActiveTexture(GL_TEXTURE1)
        glEnable(GL_BLEND)
        glEnable(GL_MULTISAMPLE)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        System.out.println("OpenGL: " + glGetString(GL_VERSION))

        // loading
        ShaderLoader.loadAll()
        TextureLoader.loadAll()

        // initialization
        GameHandler.init()
        Interface.init()
    }

    private def gameUpdate(): Unit = {
        glfwPollEvents()
        GameHandler.update()
    }

    private def gameRender(): Unit = {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

        GameHandler.render()
        Interface.render()

        glfwSwapBuffers(window)
    }

    def main(args: Array[String]): Unit = {
        Options.setLogging(args)

        val thread = new Thread {
            override def run(): Unit = {
                init()

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
                        gameUpdate()
                        u += 1
                        delta -= 1
                    }
                    gameRender()

                    f += 1
                    if (System.currentTimeMillis - timer > 1000) {
                        timer += 1000
                        Options.log("UPS: " + u + " FPS: " + f, Options.FPS)
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
