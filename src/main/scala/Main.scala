import graphics.{IndexBuffer, Renderer, Shader, Texture, VertexArray, VertexBuffer, VertexBufferLayout}
import org.lwjgl.opengl.GL11._
import input.InputHandler
import math.Matrix4f
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.{GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT, GL_DEPTH_TEST, GL_VERSION, glClear, glClearColor, glEnable, glGetString}
import org.lwjgl.opengl.GL13.{GL_TEXTURE1, glActiveTexture}
import org.lwjgl.system.MemoryUtil.NULL
import utils.{Options, Vals}
import ui.Interface

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
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glEnable(GL_DEPTH_TEST)
        glActiveTexture(GL_TEXTURE1)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        System.out.println("OpenGL: " + glGetString(GL_VERSION))
    }

    private def gameUpdate(): Unit = {
        glfwPollEvents()
    }

    private def gameRender(): Unit = {
    }

    def main(args: Array[String]): Unit = {
        Options.setLogging(args)

        val thread = new Thread {
            override def run(): Unit = {
                init()
                Interface.init

                val projMatrix = new Matrix4f().orthographic(0, Vals.WIDTH, Vals.HEIGHT, 0, -1.0f, 1.0f)
                val viewMatrix = new Matrix4f().translate(0, 0, 0)

                val va = new VertexArray
                val vb = new VertexBuffer(Array(100f, 100f, 0.0f, 0.0f,
                                                200f, 100f, 1.0f, 0.0f,
                                                200f, 200f, 1.0f, 1.0f,
                                                100f, 200f, 0.0f, 1.0f,
                                                100f, 400f, 0.0f, 1.0f,
                                                200f, 400f, 1.0f, 1.0f,
                                                200f, 500f, 1.0f, 0.0f,
                                                100f, 500f, 0.0f, 0.0f))
                val layout = new VertexBufferLayout
                val ib = new IndexBuffer(Array(0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4), 12)

                layout.pushFloat(2)
                layout.pushFloat(2)
                va.addBuffer(vb, layout)

                Shader.loadShader("Basic")
                Shader.loadShader("UI")
                Shader.get("UI").bind()
                Shader.get("UI").loadUniformMat4f("u_MVP", new Matrix4f().orthographic(0, Vals.WIDTH, Vals.HEIGHT, 0, -1.0f, 1.0f))
                val samplers = new Array[Int](32)
                for (i <- 0 until 32) samplers(i) = i
                Shader.get("UI").loadUniformIntV("u_Textures", samplers)
                val tex = new Texture("logo")

                val renderer = new Renderer

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

                    renderer.clear()
                    //Shader.get("UI").bind()
                    //Shader.get("Basic").loadUniformVec2f("mousePos", InputHandler.mousePos)
                    //Shader.get("UI").loadUniformMat4f("u_MVP", projMatrix.multiply(viewMatrix))
                    //tex.bind()
                    //Shader.get("Basic").loadUniformInt("u_Texture", 0)
                    //renderer.draw(va, ib)
                    //Shader.get("UI").loadUniformMat4f("u_MVP", projMatrix.multiply(new Matrix4f().translate(200, 0, 0)))
                    //renderer.draw(va, ib)
                    Interface.render()

                    glfwSwapBuffers(window)

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
