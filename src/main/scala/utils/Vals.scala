package utils

import game.GameHandler
import utils.math.{Mat4, Vec2, Vec3, Vec4}
import org.lwjgl.opengl.GL11._

object Vals {

    final val UNIT: Int = 100
    final val WIDTH: Int = 16 * UNIT
    final val HEIGHT: Int = 9 * UNIT

    final val UI_COLOR: Vec4 = Vec4(0.3f, 0.4f, 0.5f, 1.0f)

    final val MIN_CAMERA_PITCH: Float = 0.1f
    final val MAX_CAMERA_PITCH: Float = 1.5f
    final val MIN_CAMERA_HEIGHT: Float = 10
    final val MAX_CAMERA_HEIGHT: Float = 1000
    final val CAMERA_MOVE_SPEED: Float = 0.005f
    final val CAMERA_MOVE_SMOOTH_FACTOR: Int = 10
    
    final val MAX_RAY_DISTANCE: Float = 500
    
    final val CAMERA_STANDARD_ORIENTATION: Vec3 = Vec3(Vals.MIN_CAMERA_PITCH, 0f, 100.0f)

    final val CONTROL_POINT_COLOR: Vec4 = Vec4(0, 0.4f, 0.8f, 1)

    final val ROAD_HEIGHT: Float = 0.2f
    final val ROAD_VERTEX_DENSITY: Float = 0.5f
    final val LARGE_LANE_WIDTH: Float = 3.7f

    def getSizeOf(t: Int): Int = t match {
        case GL_UNSIGNED_INT => 4
        case GL_FLOAT => 4
        case GL_UNSIGNED_BYTE => 1
    }

    def restrain(value: Float, min: Float, max: Float): Float = if(value < min) min else if(value > max) max else value
    
    def square(value: Float): Float = value * value
    
    def center(v: Float, r: Float): Float = {
        val f = v % (2 * r)
        if(f > r) f - 2 * r else if(f < -r) f + 2 * r else f
    }
    
    def toRadians(deg: Float) = deg * Math.PI.toFloat / 180
    
    def getRay(vec: Vec2) = {
        val eyeVector = perspectiveMatrix.invert.multiply(vec.toScreenVector.fill(1.0f, 1.0f))
        val worldVector = GameHandler.camera.getViewMatrix.invert.multiply(new Vec4(eyeVector.x, eyeVector.y, -1f, 0))
        worldVector.xyz.normalize.negate
    }
    
    // heightMap should return positive infinity outside its borders
    def terrainRayCollision(ray: Vec3, heightMap: (Float, Float) => Float, accuracy: Float) = {
        val camPos = GameHandler.camera.getCameraPos
        var v: Vec3 = new Vec3
        var f = -camPos.y / ray.y
        var g = 0.0f
        
        if(f > g) {
            while(ray.scale(f-g).length > accuracy) {
                val a = (f+g)/2.0f
                v = camPos.add(ray.scale(a))
                if(v.y > heightMap(v.x, v.z)) g = a
                else f = a
            }
            new Vec3(v.x, heightMap(v.x, v.z), v.z)
        } else {
            camPos.add(ray.scale(Vals.MAX_RAY_DISTANCE))
        }
    }
    
    val perspectiveMatrix = Mat4.perspective(30, 1f, 5000f)
    val UIProjMatrix = Mat4.orthographic(0, Vals.WIDTH, Vals.HEIGHT, 0, -1.0f, 1.0f)

    val defaultTerrainLineColor = Vec4(1.0f, 0.0f, 0.2f, 0.8f)
    
}
