package utils

import org.lwjgl.opengl.GL11._

object Vars {

    final val WIDTH: Int = 1280
    final val HEIGHT: Int = 720

    def getSizeOf(t: Int): Int = t match {
        case GL_UNSIGNED_INT => 4
        case GL_FLOAT => 4
        case GL_UNSIGNED_BYTE => 1
    }

}
