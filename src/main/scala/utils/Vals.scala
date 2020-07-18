package utils

import game.GameHandler
import utils.math.{Matrix4f, Vector2f, Vector3f, Vector4f}
import org.lwjgl.opengl.GL11._

object Vals {

    final val UNIT: Int = 80
    final val WIDTH: Int = 16 * UNIT
    final val HEIGHT: Int = 9 * UNIT

    final val UI_COLOR: Vector4f = new Vector4f(0.3f, 0.4f, 0.5f, 1.0f)

    final val MIN_CAMERA_PITCH: Float = 0.1f
    final val MAX_CAMERA_PITCH: Float = 1.5f
    final val CAMERA_MOVE_SPEED: Float = 0.005f
    final val CAMERA_MOVE_SMOOTH_FACTOR: Int = 10
    
    final val MAX_RAY_DISTANCE: Float = 500
    
    final val CAMERA_STANDARD_ORIENTATION: Vector3f = new Vector3f(Vals.MIN_CAMERA_PITCH, 0f, 10.0f)

    def getSizeOf(t: Int): Int = t match {
        case GL_UNSIGNED_INT => 4
        case GL_FLOAT => 4
        case GL_UNSIGNED_BYTE => 1
    }

    def restrain(value: Float, min: Float, max: Float): Float = if(value < min) min else if(value > max) max else value
    
    def square(value: Float) = value * value
    
    def center(v: Float, r: Float) = {
        val f = v % (2 * r)
        if(f > r) f - 2 * r else if(f < -r) f + 2 * r else f
    }
    
    def toRadians(deg: Float) = deg * Math.PI.toFloat / 180
    
    def getRay(vec: Vector2f) = {
        
        val eyeVector = perspectiveMatrix.invert.multiply(vec.toScreenVector.fill(1.0f, 1.0f))
        val worldVector = GameHandler.camera.getViewMatrix.invert.multiply(new Vector4f(eyeVector.x, eyeVector.y, -1f, 0))
        worldVector.xyz.normalize.negate
    }
    
    // heightMap should return positive infinity outside its borders
    def terrainRayCollision(ray: Vector3f, heightMap: (Float, Float) => Float, accuracy: Float) = {
        val camPos = GameHandler.camera.getCameraPos
        var v: Vector3f = new Vector3f
        var f = -camPos.y / ray.y
        var g = 0.0f
        
        if(f > g) {
            while(ray.scale(f-g).length > accuracy) {
                val a = (f+g)/2.0f
                v = camPos.add(ray.scale(a))
                if(v.y > heightMap(v.x, v.z)) g = a
                else f = a
            }
            new Vector3f(v.x, heightMap(v.x, v.z), v.z)
        } else {
            camPos.add(ray.scale(Vals.MAX_RAY_DISTANCE))
        }
        
        
    }
    
    val perspectiveMatrix = Matrix4f.perspective(30, 0.1f, 10000f)
    val UIProjMatrix = Matrix4f.orthographic(0, Vals.WIDTH, Vals.HEIGHT, 0, -1.0f, 1.0f)
    
    
    
}
