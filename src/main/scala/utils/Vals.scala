package utils

import utils.math.{Matrix4f, Vector3f, Vector4f}
import org.lwjgl.opengl.GL11._

object Vals {

    final val UNIT: Int = 80
    final val WIDTH: Int = 16 * UNIT
    final val HEIGHT: Int = 9 * UNIT

    final val UI_COLOR: Vector4f = new Vector4f(0.3f, 0.4f, 0.5f, 1.0f)

    def getSizeOf(t: Int): Int = t match {
        case GL_UNSIGNED_INT => 4
        case GL_FLOAT => 4
        case GL_UNSIGNED_BYTE => 1
    }

    def restrain(value: Float, min: Float, max: Float): Float = if(value < min) min else if(value > max) max else value

    val perspectiveMatrix = Matrix4f.perspective(30, 0.1f, 10000f)
    val UIProjMatrix = Matrix4f.orthographic(0, Vals.WIDTH, Vals.HEIGHT, 0, -1.0f, 1.0f)

}
