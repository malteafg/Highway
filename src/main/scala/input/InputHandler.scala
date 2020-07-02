package input

import org.lwjgl.glfw.GLFW.glfwGetCursorPos

object InputHandler {

    var x: Int = 0
    var y: Int = 0

    def keyPressed(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        print(s"Key \'${key.toChar}\' was pressed \n")
    }

    def mousePressed(window: Long, button: Int, action: Int, mods: Int): Unit = {
        print(s"Button ${button} was pressed on (${x}, ${y}) \n")
    }

    def mouseMoved(window: Long, xpos: Double, ypos: Double): Unit = {
        x = xpos.toInt
        y = ypos.toInt
        print(s"Mouse was moved to (${x}, ${y}) \n")
    }

}
