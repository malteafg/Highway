import org.lwjgl.glfw.GLFW.{GLFW_BLUE_BITS, GLFW_GREEN_BITS, GLFW_RED_BITS, GLFW_REFRESH_RATE, glfwCreateWindow, glfwGetPrimaryMonitor, glfwGetVideoMode, glfwInit, glfwMakeContextCurrent, glfwSetWindowPos, glfwShowWindow, glfwWindowHint}
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.{GL_DEPTH_TEST, GL_VERSION, glClearColor, glEnable, glGetString}
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

        glfwMakeContextCurrent(window)
        glfwShowWindow(window)

        GL.createCapabilities

        // setting graphics
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glEnable(GL_DEPTH_TEST)
        glActiveTexture(GL_TEXTURE1)
        System.out.println("OpenGL: " + glGetString(GL_VERSION))
    }

    def main(args: Array[String]): Unit = {
        val thread = new Thread {
            override def run(): Unit = {
                init()
            }
        }
        thread.start()
    }

}
