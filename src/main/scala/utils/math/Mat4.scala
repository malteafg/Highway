package utils.math

import java.nio.FloatBuffer
import utils.Vals

case class Mat4(
                 m00: Float = 1, m01: Float = 0, m02: Float = 0, m03: Float = 0,
                 m10: Float = 0, m11: Float = 1, m12: Float = 0, m13: Float = 0,
                 m20: Float = 0, m21: Float = 0, m22: Float = 1, m23: Float = 0,
                 m30: Float = 0, m31: Float = 0, m32: Float = 0, m33: Float = 1
               ) {

    /**
     * Creates a 4x4 matrix with specified columns.
     *
     * @param col1 Vector with values of the first column
     * @param col2 Vector with values of the second column
     * @param col3 Vector with values of the third column
     * @param col4 Vector with values of the fourth column
     */
    def this(col1: Vec4, col2: Vec4, col3: Vec4, col4: Vec4) {
        this(
            m00 = col1.x,
            m10 = col1.y,
            m20 = col1.z,
            m30 = col1.w,
            m01 = col2.x,
            m11 = col2.y,
            m21 = col2.z,
            m31 = col2.w,
            m02 = col3.x,
            m12 = col3.y,
            m22 = col3.z,
            m32 = col3.w,
            m03 = col4.x,
            m13 = col4.y,
            m23 = col4.z,
            m33 = col4.w
        )
    }

    /**
     * Adds this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Sum of this + other
     */
    def add(other: Mat4): Mat4 = {
        Mat4(
            m00 = this.m00 + other.m00,
            m10 = this.m10 + other.m10,
            m20 = this.m20 + other.m20,
            m30 = this.m30 + other.m30,
            m01 = this.m01 + other.m01,
            m11 = this.m11 + other.m11,
            m21 = this.m21 + other.m21,
            m31 = this.m31 + other.m31,
            m02 = this.m02 + other.m02,
            m12 = this.m12 + other.m12,
            m22 = this.m22 + other.m22,
            m32 = this.m32 + other.m32,
            m03 = this.m03 + other.m03,
            m13 = this.m13 + other.m13,
            m23 = this.m23 + other.m23,
            m33 = this.m33 + other.m33
        )
    }

    /**
     * Negates this matrix.
     *
     * @return Negated matrix
     */
    def negate: Mat4 = multiply(-1f)

    /**
     * Subtracts this matrix from another matrix.
     *
     * @param other The other matrix
     * @return Difference of this - other
     */
    def subtract(other: Mat4): Mat4 = this.add(other.negate)

    /**
     * Multiplies this matrix with a scalar.
     *
     * @param scalar The scalar
     * @return Scalar product of this * scalar
     */
    def multiply(scalar: Float): Mat4 = {
        Mat4(
            m00 = this.m00 * scalar,
            m10 = this.m10 * scalar,
            m20 = this.m20 * scalar,
            m30 = this.m30 * scalar,
            m01 = this.m01 * scalar,
            m11 = this.m11 * scalar,
            m21 = this.m21 * scalar,
            m31 = this.m31 * scalar,
            m02 = this.m02 * scalar,
            m12 = this.m12 * scalar,
            m22 = this.m22 * scalar,
            m32 = this.m32 * scalar,
            m03 = this.m03 * scalar,
            m13 = this.m13 * scalar,
            m23 = this.m23 * scalar,
            m33 = this.m33 * scalar
        )
    }

    /**
     * Multiplies this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    def multiply(vector: Vec4): Vec4 = {
        val x = this.m00 * vector.x + this.m01 * vector.y + this.m02 * vector.z + this.m03 * vector.w
        val y = this.m10 * vector.x + this.m11 * vector.y + this.m12 * vector.z + this.m13 * vector.w
        val z = this.m20 * vector.x + this.m21 * vector.y + this.m22 * vector.z + this.m23 * vector.w
        val w = this.m30 * vector.x + this.m31 * vector.y + this.m32 * vector.z + this.m33 * vector.w
        Vec4(x, y, z, w)
    }

    /**
     * Transforms this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    def transform(vector: Vec4): Vec4 = {
        val x = this.m00 * vector.x + this.m10 * vector.y + this.m20 * vector.z + this.m30 * vector.w
        val y = this.m01 * vector.x + this.m11 * vector.y + this.m21 * vector.z + this.m31 * vector.w
        val z = this.m02 * vector.x + this.m12 * vector.y + this.m22 * vector.z + this.m32 * vector.w
        val w = this.m03 * vector.x + this.m13 * vector.y + this.m23 * vector.z + this.m33 * vector.w
        Vec4(x, y, z, w)
    }

    /**
     * Multiplies this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Matrix product of this * other
     */
    def multiply(other: Mat4): Mat4 = {
        Mat4(
            m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30,
            m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30,
            m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30,
            m30 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30,
            m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31,
            m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31,
            m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31,
            m31 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31,
            m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32,
            m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32,
            m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32,
            m32 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32,
            m03 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33,
            m13 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33,
            m23 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33,
            m33 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33
        )
    }

    /**
     * Transposes this matrix.
     *
     * @return Transposed matrix
     */
    def transpose: Mat4 = {
        Mat4(
            m00 = this.m00,
            m10 = this.m01,
            m20 = this.m02,
            m30 = this.m03,
            m01 = this.m10,
            m11 = this.m11,
            m21 = this.m12,
            m31 = this.m13,
            m02 = this.m20,
            m12 = this.m21,
            m22 = this.m22,
            m32 = this.m23,
            m03 = this.m30,
            m13 = this.m31,
            m23 = this.m32,
            m33 = this.m33
        )
    }

    /**
     * Stores the matrix in a given Buffer.
     *
     * @param buffer The buffer to store the matrix data
     */
    def toBuffer(buffer: FloatBuffer): FloatBuffer = {
        buffer.put(m00).put(m10).put(m20).put(m30)
        buffer.put(m01).put(m11).put(m21).put(m31)
        buffer.put(m02).put(m12).put(m22).put(m32)
        buffer.put(m03).put(m13).put(m23).put(m33)
        buffer.flip
    }

    /**
     * Invert this 4x4 matrix.
     */
    def invert: Mat4 = {
        val tmp = new Array[Float](12)
        val src = new Array[Float](16)
        val dst = new Array[Float](16)
        // Transpose matrix
        src(0) = this.m00
        src(4) = this.m01
        src(8) = this.m02
        src(12) = this.m03
        src(1) = this.m10
        src(5) = this.m11
        src(9) = this.m12
        src(13) = this.m13
        src(2) = this.m20
        src(6) = this.m21
        src(10) = this.m22
        src(14) = this.m23
        src(3) = this.m30
        src(7) = this.m31
        src(11) = this.m32
        src(15) = this.m33
        // Calculate pairs for first 8 elements (cofactors)
        tmp(0) = src(10) * src(15)
        tmp(1) = src(11) * src(14)
        tmp(2) = src(9) * src(15)
        tmp(3) = src(11) * src(13)
        tmp(4) = src(9) * src(14)
        tmp(5) = src(10) * src(13)
        tmp(6) = src(8) * src(15)
        tmp(7) = src(11) * src(12)
        tmp(8) = src(8) * src(14)
        tmp(9) = src(10) * src(12)
        tmp(10) = src(8) * src(13)
        tmp(11) = src(9) * src(12)
        // Calculate first 8 elements (cofactors)
        dst(0) = tmp(0) * src(5) + tmp(3) * src(6) + tmp(4) * src(7)
        dst(0) -= tmp(1) * src(5) + tmp(2) * src(6) + tmp(5) * src(7)
        dst(1) = tmp(1) * src(4) + tmp(6) * src(6) + tmp(9) * src(7)
        dst(1) -= tmp(0) * src(4) + tmp(7) * src(6) + tmp(8) * src(7)
        dst(2) = tmp(2) * src(4) + tmp(7) * src(5) + tmp(10) * src(7)
        dst(2) -= tmp(3) * src(4) + tmp(6) * src(5) + tmp(11) * src(7)
        dst(3) = tmp(5) * src(4) + tmp(8) * src(5) + tmp(11) * src(6)
        dst(3) -= tmp(4) * src(4) + tmp(9) * src(5) + tmp(10) * src(6)
        dst(4) = tmp(1) * src(1) + tmp(2) * src(2) + tmp(5) * src(3)
        dst(4) -= tmp(0) * src(1) + tmp(3) * src(2) + tmp(4) * src(3)
        dst(5) = tmp(0) * src(0) + tmp(7) * src(2) + tmp(8) * src(3)
        dst(5) -= tmp(1) * src(0) + tmp(6) * src(2) + tmp(9) * src(3)
        dst(6) = tmp(3) * src(0) + tmp(6) * src(1) + tmp(11) * src(3)
        dst(6) -= tmp(2) * src(0) + tmp(7) * src(1) + tmp(10) * src(3)
        dst(7) = tmp(4) * src(0) + tmp(9) * src(1) + tmp(10) * src(2)
        dst(7) -= tmp(5) * src(0) + tmp(8) * src(1) + tmp(11) * src(2)
        // Calculate pairs for second 8 elements (cofactors)
        tmp(0) = src(2) * src(7)
        tmp(1) = src(3) * src(6)
        tmp(2) = src(1) * src(7)
        tmp(3) = src(3) * src(5)
        tmp(4) = src(1) * src(6)
        tmp(5) = src(2) * src(5)
        tmp(6) = src(0) * src(7)
        tmp(7) = src(3) * src(4)
        tmp(8) = src(0) * src(6)
        tmp(9) = src(2) * src(4)
        tmp(10) = src(0) * src(5)
        tmp(11) = src(1) * src(4)
        // Calculate second 8 elements (cofactors)
        dst(8) = tmp(0) * src(13) + tmp(3) * src(14) + tmp(4) * src(15)
        dst(8) -= tmp(1) * src(13) + tmp(2) * src(14) + tmp(5) * src(15)
        dst(9) = tmp(1) * src(12) + tmp(6) * src(14) + tmp(9) * src(15)
        dst(9) -= tmp(0) * src(12) + tmp(7) * src(14) + tmp(8) * src(15)
        dst(10) = tmp(2) * src(12) + tmp(7) * src(13) + tmp(10) * src(15)
        dst(10) -= tmp(3) * src(12) + tmp(6) * src(13) + tmp(11) * src(15)
        dst(11) = tmp(5) * src(12) + tmp(8) * src(13) + tmp(11) * src(14)
        dst(11) -= tmp(4) * src(12) + tmp(9) * src(13) + tmp(10) * src(14)
        dst(12) = tmp(2) * src(10) + tmp(5) * src(11) + tmp(1) * src(9)
        dst(12) -= tmp(4) * src(11) + tmp(0) * src(9) + tmp(3) * src(10)
        dst(13) = tmp(8) * src(11) + tmp(0) * src(8) + tmp(7) * src(10)
        dst(13) -= tmp(6) * src(10) + tmp(9) * src(11) + tmp(1) * src(8)
        dst(14) = tmp(6) * src(9) + tmp(11) * src(11) + tmp(3) * src(8)
        dst(14) -= tmp(10) * src(11) + tmp(2) * src(8) + tmp(7) * src(9)
        dst(15) = tmp(10) * src(10) + tmp(4) * src(8) + tmp(9) * src(9)
        dst(15) -= tmp(8) * src(9) + tmp(11) * src(10) + tmp(5) * src(8)
        // Calculate determinant
        var det = src(0) * dst(0) + src(1) * dst(1) + src(2) * dst(2) + src(3) * dst(3)
        // Calculate matrix inverse
        det = 1.0f / det
        Mat4(
            m00 = dst(0) * det,
            m01 = dst(1) * det,
            m02 = dst(2) * det,
            m03 = dst(3) * det,
            m10 = dst(4) * det,
            m11 = dst(5) * det,
            m12 = dst(6) * det,
            m13 = dst(7) * det,
            m20 = dst(8) * det,
            m21 = dst(9) * det,
            m22 = dst(10) * det,
            m23 = dst(11) * det,
            m30 = dst(12) * det,
            m31 = dst(13) * det,
            m32 = dst(14) * det,
            m33 = dst(15) * det
        )
    }

    /**
     * Multiplies this matrix with a translation matrix. Similar to
     * <code>glTranslate(x, y, z)</code>.
     *
     * @param x x coordinate of translation vector
     * @param y y coordinate of translation vector
     * @param z z coordinate of translation vector
     * @return Translation matrix
     */
    def translate(x: Float, y: Float, z: Float): Mat4 = {
        this.multiply(Mat4.translate(x, y, z))
    }

    /**
     * Multiplies this matrix with a translation matrix. Similar to
     * <code>glTranslate(x, y, z)</code>.
     *
     * @param v Vector3f
     * @return Translation matrix
     */
    def translate(v: Vec3): Mat4 = {
        this.multiply(Mat4.translate(v.x, v.y, v.z))
    }

    /**
     * Multiplies this matrix with a rotation matrix. Similar to
     * <code>glRotate(angle, x, y, z)</code>.
     *
     * @param angle  Angle of rotation in radians
     * @param nx     x coordinate of the rotation vector
     * @param ny     y coordinate of the rotation vector
     * @param nz     z coordinate of the rotation vector
     * @return Rotation matrix
     */
    def rotate(angle: Float, nx: Float, ny: Float, nz: Float): Mat4 = {
        this.multiply(Mat4.rotate(angle, nx, ny, nz))
    }

    /**
     * Multiplies this matrix with a scaling matrix. Similar to <code>glScale(x, y, z)</code>.
     *
     * @param s Scale factor along all axis
     * @return Scaling matrix
     */
    def scale(s: Float): Mat4 = this.multiply(Mat4.scale(s, s, s))

    /**
     * Multiplies this matrix with a scaling matrix. Similar to <code>glScale(x, y, z)</code>.
     *
     * @param x Scale factor along the x coordinate
     * @param y Scale factor along the y coordinate
     * @param z Scale factor along the z coordinate
     * @return Scaling matrix
     */
    def scale(x: Float, y: Float, z: Float): Mat4 = {
        this.multiply(Mat4.scale(x, y, z))
    }

}

object Mat4 {

    /**
     * Creates a orthographic projection matrix. Similar to
     * <code>glOrtho(left, right, bottom, top, near, far)</code>.
     *
     * @param left   Coordinate for the left vertical clipping pane
     * @param right  Coordinate for the right vertical clipping pane
     * @param bottom Coordinate for the bottom horizontal clipping pane
     * @param top    Coordinate for the bottom horizontal clipping pane
     * @param near   Coordinate for the near depth clipping pane
     * @param far    Coordinate for the far depth clipping pane
     * @return Orthographic matrix
     */
    def orthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Mat4 = {
        Mat4(
            m00 = 2.0f / (right - left),
            m11 = 2.0f / (top - bottom),
            m22 = 2.0f / (near - far),
            m03 = (left + right) / (left - right),
            m13 = (bottom + top) / (bottom - top),
            m23 = (far + near) / (far - near)
        )
    }

    /**
     * Creates a perspective projection matrix. Similar to
     * <code>glFrustum(left, right, bottom, top, near, far)</code>.
     *
     * @param left   Coordinate for the left vertical clipping pane
     * @param right  Coordinate for the right vertical clipping pane
     * @param bottom Coordinate for the bottom horizontal clipping pane
     * @param top    Coordinate for the bottom horizontal clipping pane
     * @param near   Coordinate for the near depth clipping pane, must be
     *               positive
     * @param far    Coordinate for the far depth clipping pane, must be
     *               positive
     * @return Perspective matrix
     */
    def frustum(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Mat4 = {
        val a = (right + left) / (right - left)
        val b = (top + bottom) / (top - bottom)
        val c = -(far + near) / (far - near)
        val d = -(2f * far * near) / (far - near)
        Mat4(
            m00 = (2f * near) / (right - left),
            m11 = (2f * near) / (top - bottom),
            m02 = a,
            m12 = b,
            m22 = c,
            m32 = -1f,
            m23 = d,
        )
    }

    /**
     * Creates a perspective projection matrix. Similar to
     * <code>gluPerspective(fovy, aspec, zNear, zFar)</code>.
     *
     * @param fovy   Field of view angle in degrees
     * @param near   Distance from the viewer to the near clipping plane, must
     *               be positive
     * @param far    Distance from the viewer to the far clipping plane, must be
     *               positive
     * @return Perspective matrix
     */
    def perspective(fovy: Float, near: Float, far: Float): Mat4 = {
        val ar = 1.0f * Vals.WIDTH / Vals.HEIGHT
        val range = near - far
        val tanHalfFOV = Math.tan(Math.toRadians(fovy / 2)).toFloat

        Mat4(
            m00 = 1.0f / tanHalfFOV / ar,
            m11 = 1.0f / tanHalfFOV,
            m22 = (-near - far) / range,
            m23 = 2.0f * far * near / range,
            m32 = 1f,
        )
    }

    /**
     * Creates a translation matrix with rotation around the vertical y axis.
     *
     * @param x x coordinate of translation vector
     * @param y y coordinate of translation vector
     * @param z z coordinate of translation vector
     * @param a angle of rotation
     * @return Translation matrix
     */
    def place(x: Float, y: Float, z: Float, a: Float): Mat4 = {
        translate(x, y, z).rotate(a, 0, 1, 0)
    }

    /**
     * Creates a translation matrix. Similar to
     * <code>glTranslate(x, y, z)</code>.
     *
     * @param v: Vector to translate
     * @return Translation matrix
     */
    def translate(v: Vec3): Mat4 = translate(v.x, v.y, v.z)

    /**
     * Creates a translation matrix. Similar to
     * <code>glTranslate(x, y, z)</code>.
     *
     * @param x x coordinate of translation vector
     * @param y y coordinate of translation vector
     * @param z z coordinate of translation vector
     * @return Translation matrix
     */
    def translate(x: Float, y: Float, z: Float): Mat4 = {
        Mat4(
            m03 = x,
            m13 = y,
            m23 = z
        )
    }

    /**
     * Creates a rotation matrix. Similar to
     * <code>glRotate(angle, x, y, z)</code>.
     *
     * @param angle  Angle of rotation in radians
     * @param nx     x coordinate of the rotation vector
     * @param ny     y coordinate of the rotation vector
     * @param nz     z coordinate of the rotation vector
     * @return Rotation matrix
     */
    def rotate(angle: Float, nx: Float, ny: Float, nz: Float): Mat4 = {
        val c = Math.cos(angle).toFloat
        val s = Math.sin(angle).toFloat
        val vec = Vec3(nx, ny, nz).normalize
        val x = vec.x
        val y = vec.y
        val z = vec.z
        Mat4(
            m00 = x * x * (1f - c) + c,
            m10 = y * x * (1f - c) + z * s,
            m20 = x * z * (1f - c) - y * s,
            m01 = x * y * (1f - c) - z * s,
            m11 = y * y * (1f - c) + c,
            m21 = y * z * (1f - c) + x * s,
            m02 = x * z * (1f - c) + y * s,
            m12 = y * z * (1f - c) - x * s,
            m22 = z * z * (1f - c) + c
        )
    }

    def direction(dir: Vec3): Mat4 = new Mat4(new Vec4(dir, 0), new Vec4(dir.cross(dir.leftHand()).negate, 0), new Vec4(dir.leftHand(), 0) , Vec4(0, 0, 0, 1f))

    /**
     * Creates a scaling matrix. Similar to <code>glScale(x, y, z)</code>.
     *
     * @param s Scale factor along the all axis
     * @return Scaling matrix
     */
    def scale(s: Float): Mat4 = scale(s, s, s)

    /**
     * Creates a scaling matrix. Similar to <code>glScale(x, y, z)</code>.
     *
     * @param x Scale factor along the x coordinate
     * @param y Scale factor along the y coordinate
     * @param z Scale factor along the z coordinate
     * @return Scaling matrix
     */
    def scale(x: Float, y: Float, z: Float): Mat4 = Mat4(m00 = x, m11 = y, m22 = z)

    /**
     * Returns empty matrix.
     */
    def empty(): Mat4 = Mat4(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

}
