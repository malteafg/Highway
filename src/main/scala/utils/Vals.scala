package utils

import game.GameHandler
import utils.math.{Mat4, Vec2, Vec3, Vec4}
import org.lwjgl.opengl.GL11._

object Vals {

    final val UNIT: Int = 100
    final val WIDTH: Int = 16 * UNIT
    final val HEIGHT: Int = 9 * UNIT

    final val UI_COLOR: Vec4 = Vec4(0.3f, 0.4f, 0.5f, 1.0f)

    final val PRETTY_CLOSE: Float = 0.95f

    final val MIN_CAMERA_PITCH: Float = 0.15f
    final val MAX_CAMERA_PITCH: Float = 1.5f
    final val MIN_CAMERA_HEIGHT: Float = 10
    final val MAX_CAMERA_HEIGHT: Float = 1000
    final val MAX_CAMERA_SPEED: Int = 10
    final val CAMERA_MOVE_SPEED: Float = 0.005f
    final val CAMERA_MOVE_SMOOTH_FACTOR: Int = 10

    final val MAX_RAY_DISTANCE: Float = 500

    final val CAMERA_STANDARD_ORIENTATION: Vec3 = Vec3(Vals.MIN_CAMERA_PITCH, 0f, 100.0f)

    final val CAMERA_TOPDOWN_ORIENTATION: Vec3 = Vec3(Vals.MAX_CAMERA_PITCH, 0f, 500.0f)

    final val CONTROL_POINT_COLOR: Vec4 = Vec4(0, 0.4f, 0.8f, 1)

    final val ROAD_HEIGHT: Float = 0.2f
    final val ROAD_VERTEX_DENSITY: Float = 0.25f
    final val ROAD_VERTEX_MINIMUM: Int = 10
    final val LARGE_LANE_WIDTH: Float = 3.7f
    final val MIN_SEGMENT_LENGTH: Float = 10f

    def getSizeOf(t: Int): Int = t match {
        case GL_UNSIGNED_INT => 4
        case GL_FLOAT => 4
        case GL_UNSIGNED_BYTE => 1
    }

    def restrain(value: Float, min: Float, max: Float): Float = if (value < min) min else if (value > max) max else value

    def square(value: Float): Float = value * value

    def stepSum(value: Int): Int = value * (Math.abs(value) + 1) / 2

    def max(f: Float*): Float = f.fold[Float](0f)((a: Float, b: Float) => Math.max(a, b))
    def min(f: Float*): Float = f.fold[Float](0f)((a: Float, b: Float) => Math.min(a, b))

    def center(v: Float, r: Float): Float = {
        val f = v % (2 * r)
        if (f > r) f - 2 * r else if (f < -r) f + 2 * r else f
    }

    def rel(a: Float, b: Float): Float = Math.min(a, b) / Math.max(a, b)

    def sCurveSegmentLength(v1: Vec3, r1: Vec3, v2: Vec3, r2: Vec3): Float = {
        val v = v2.subtract(v1)
        val r = r2.subtract(r1)
        val k = v.dot(r) / (4f - r.lengthSquared)
        k + Math.sqrt(v.lengthSquared/(4f - r.lengthSquared) + k * k).toFloat
    }

    def toRadians(deg: Float): Float = deg * Math.PI.toFloat / 180

    def minRoadLength(d1: Vec3, d2: Vec3, laneCount: Int): Float = Math.max(MIN_SEGMENT_LENGTH, LARGE_LANE_WIDTH * laneCount * 3 * d1.antiDot(d2))

    def isCurveTooSmall(d1: Vec3, d2: Vec3, laneCount: Int): Boolean = d2.length < minRoadLength(d1, d2, laneCount)

    def minCurveCornerLength(d1: Vec3, d2: Vec3, laneCount: Int): Float = {
        val v = d1.antiDot(d2)
        val w = d1.normalize.dot(d2.normalize)
        Math.max(MIN_SEGMENT_LENGTH, LARGE_LANE_WIDTH * laneCount * 3 * Math.min(1, v / (2 - 2 * w)))
    }

    def getRay(vec: Vec2): Vec3 = {
        val eyeVector = perspectiveMatrix.invert.multiply(vec.toScreenVector.fill(1.0f, 1.0f))
        val worldVector = GameHandler.camera.getViewMatrix.invert.multiply(Vec4(eyeVector.x, eyeVector.y, -1f))
        worldVector.xyz.normalize.negate
    }

    // heightMap should return positive infinity outside its borders
    def terrainRayCollision(ray: Vec3, heightMap: (Float, Float) => Float, accuracy: Float): Vec3 = {
        val camPos = GameHandler.camera.getCameraPos
        var v: Vec3 = new Vec3
        var f = -camPos.y / ray.y
        var g = 0.0f

        if (f > g) {
            while (ray.scale(f - g).length > accuracy) {
                val a = (f + g) / 2.0f
                v = camPos.add(ray.scale(a))
                if (v.y > heightMap(v.x, v.z)) g = a
                else f = a
            }
            Vec3(v.x, heightMap(v.x, v.z), v.z)
        } else {
            camPos.add(ray.scale(Vals.MAX_RAY_DISTANCE))
        }
    }

    val perspectiveMatrix: Mat4 = Mat4.perspective(30, 2f, 4000f)
    val UIProjMatrix: Mat4 = Mat4.orthographic(0, Vals.WIDTH, Vals.HEIGHT, 0, -1.0f, 1.0f)

    val defaultTerrainLineColor: Vec4 = Vec4(0.2f, 0.3f, 0.9f, 0.3f)

}
