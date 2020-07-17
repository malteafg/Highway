package utils.math

import java.nio.FloatBuffer
import utils.Vals

class Matrix4f(var m00: Float, var m01: Float, var m02: Float, var m03: Float,
               var m10: Float, var m11: Float, var m12: Float, var m13: Float,
               var m20: Float, var m21: Float, var m22: Float, var m23: Float,
               var m30: Float, var m31: Float, var m32: Float, var m33: Float) {

    /**
     * Creates a 4x4 identity matrix.
     */
    def this() = this(1f, 0f, 0f, 0f,
                      0f, 1f, 0f, 0f,
                      0f, 0f, 1f, 0f,
                      0f, 0f, 0f, 1f)

    /**
     * Creates a 4x4 matrix with specified columns.
     *
     * @param col1 Vector with values of the first column
     * @param col2 Vector with values of the second column
     * @param col3 Vector with values of the third column
     * @param col4 Vector with values of the fourth column
     */
    def this(col1: Vector4f, col2: Vector4f, col3: Vector4f, col4: Vector4f) {
        this()
        m00 = col1.x
        m10 = col1.y
        m20 = col1.z
        m30 = col1.w
        m01 = col2.x
        m11 = col2.y
        m21 = col2.z
        m31 = col2.w
        m02 = col3.x
        m12 = col3.y
        m22 = col3.z
        m32 = col3.w
        m03 = col4.x
        m13 = col4.y
        m23 = col4.z
        m33 = col4.w
    }

    /**
     * Sets this matrix to the identity matrix.
     */
    def setIdentity(): Unit = {
        m00 = 1f
        m11 = 1f
        m22 = 1f
        m33 = 1f
        m01 = 0f
        m02 = 0f
        m03 = 0f
        m10 = 0f
        m12 = 0f
        m13 = 0f
        m20 = 0f
        m21 = 0f
        m23 = 0f
        m30 = 0f
        m31 = 0f
        m32 = 0f
    }

    /**
     * Adds this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Sum of this + other
     */
    def add(other: Matrix4f): Matrix4f = {
        val result = new Matrix4f
        result.m00 = this.m00 + other.m00
        result.m10 = this.m10 + other.m10
        result.m20 = this.m20 + other.m20
        result.m30 = this.m30 + other.m30
        result.m01 = this.m01 + other.m01
        result.m11 = this.m11 + other.m11
        result.m21 = this.m21 + other.m21
        result.m31 = this.m31 + other.m31
        result.m02 = this.m02 + other.m02
        result.m12 = this.m12 + other.m12
        result.m22 = this.m22 + other.m22
        result.m32 = this.m32 + other.m32
        result.m03 = this.m03 + other.m03
        result.m13 = this.m13 + other.m13
        result.m23 = this.m23 + other.m23
        result.m33 = this.m33 + other.m33
        result
    }

    /**
     * Negates this matrix.
     *
     * @return Negated matrix
     */
    def negate: Matrix4f = multiply(-1f)

    /**
     * Subtracts this matrix from another matrix.
     *
     * @param other The other matrix
     * @return Difference of this - other
     */
    def subtract(other: Matrix4f): Matrix4f = this.add(other.negate)

    /**
     * Multiplies this matrix with a scalar.
     *
     * @param scalar The scalar
     * @return Scalar product of this * scalar
     */
    def multiply(scalar: Float): Matrix4f = {
        val result = new Matrix4f
        result.m00 = this.m00 * scalar
        result.m10 = this.m10 * scalar
        result.m20 = this.m20 * scalar
        result.m30 = this.m30 * scalar
        result.m01 = this.m01 * scalar
        result.m11 = this.m11 * scalar
        result.m21 = this.m21 * scalar
        result.m31 = this.m31 * scalar
        result.m02 = this.m02 * scalar
        result.m12 = this.m12 * scalar
        result.m22 = this.m22 * scalar
        result.m32 = this.m32 * scalar
        result.m03 = this.m03 * scalar
        result.m13 = this.m13 * scalar
        result.m23 = this.m23 * scalar
        result.m33 = this.m33 * scalar
        result
    }

    /**
     * Multiplies this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    def multiply(vector: Vector4f): Vector4f = {
        val x = this.m00 * vector.x + this.m01 * vector.y + this.m02 * vector.z + this.m03 * vector.w
        val y = this.m10 * vector.x + this.m11 * vector.y + this.m12 * vector.z + this.m13 * vector.w
        val z = this.m20 * vector.x + this.m21 * vector.y + this.m22 * vector.z + this.m23 * vector.w
        val w = this.m30 * vector.x + this.m31 * vector.y + this.m32 * vector.z + this.m33 * vector.w
        new Vector4f(x, y, z, w)
    }

    /**
     * Transforms this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    def transform(vector: Vector4f): Vector4f = {
        val x = this.m00 * vector.x + this.m10 * vector.y + this.m20 * vector.z + this.m30 * vector.w
        val y = this.m01 * vector.x + this.m11 * vector.y + this.m21 * vector.z + this.m31 * vector.w
        val z = this.m02 * vector.x + this.m12 * vector.y + this.m22 * vector.z + this.m32 * vector.w
        val w = this.m03 * vector.x + this.m13 * vector.y + this.m23 * vector.z + this.m33 * vector.w
        new Vector4f(x, y, z, w)
    }

    /**
     * Multiplies this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Matrix product of this * other
     */
    def multiply(other: Matrix4f): Matrix4f = {
        val result = new Matrix4f
        result.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30
        result.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30
        result.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30
        result.m30 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30
        result.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31
        result.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31
        result.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31
        result.m31 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31
        result.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32
        result.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32
        result.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32
        result.m32 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32
        result.m03 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33
        result.m13 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33
        result.m23 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33
        result.m33 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33
        result
    }

    /**
     * Transposes this matrix.
     *
     * @return Transposed matrix
     */
    def transpose: Matrix4f = {
        val result = new Matrix4f
        result.m00 = this.m00
        result.m10 = this.m01
        result.m20 = this.m02
        result.m30 = this.m03
        result.m01 = this.m10
        result.m11 = this.m11
        result.m21 = this.m12
        result.m31 = this.m13
        result.m02 = this.m20
        result.m12 = this.m21
        result.m22 = this.m22
        result.m32 = this.m23
        result.m03 = this.m30
        result.m13 = this.m31
        result.m23 = this.m32
        result.m33 = this.m33
        result
    }

    /**
     * Stores the matrix in a given Buffer.
     *
     * @param buffer The buffer to store the matrix data
     */
    def toBuffer(buffer: FloatBuffer): Unit = {
        buffer.put(m00).put(m10).put(m20).put(m30)
        buffer.put(m01).put(m11).put(m21).put(m31)
        buffer.put(m02).put(m12).put(m22).put(m32)
        buffer.put(m03).put(m13).put(m23).put(m33)
        buffer.flip
    }

    /**
     * Invert this 4x4 matrix.
     */
    def invert: Matrix4f = {
        val invert = new Matrix4f
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
        invert.m00 = dst(0) * det
        invert.m01 = dst(1) * det
        invert.m02 = dst(2) * det
        invert.m03 = dst(3) * det
        invert.m10 = dst(4) * det
        invert.m11 = dst(5) * det
        invert.m12 = dst(6) * det
        invert.m13 = dst(7) * det
        invert.m20 = dst(8) * det
        invert.m21 = dst(9) * det
        invert.m22 = dst(10) * det
        invert.m23 = dst(11) * det
        invert.m30 = dst(12) * det
        invert.m31 = dst(13) * det
        invert.m32 = dst(14) * det
        invert.m33 = dst(15) * det
        invert
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
    def translate(x: Float, y: Float, z: Float): Matrix4f = {
        this.multiply(Matrix4f.translate(x, y, z))
    }

    /**
     * Multiplies this matrix with a translation matrix. Similar to
     * <code>glTranslate(x, y, z)</code>.
     *
     * @param v Vector3f
     * @return Translation matrix
     */
    def translate(v: Vector3f): Matrix4f = {
        this.multiply(Matrix4f.translate(v.x, v.y, v.z))
    }

    /**
     * Multiplies this matrix with a rotation matrix. Similar to
     * <code>glRotate(angle, x, y, z)</code>.
     *
     * @param angle Angle of rotation in degrees
     * @param nx     x coordinate of the rotation vector
     * @param ny     y coordinate of the rotation vector
     * @param nz     z coordinate of the rotation vector
     * @return Rotation matrix
     */
    def rotate(angle: Float, nx: Float, ny: Float, nz: Float): Matrix4f = {
        this.multiply(Matrix4f.rotate(angle, nx, ny, nz))
    }

    /**
     * Multiplies this matrix with a scaling matrix. Similar to <code>glScale(x, y, z)</code>.
     *
     * @param x Scale factor along the x coordinate
     * @param y Scale factor along the y coordinate
     * @param z Scale factor along the z coordinate
     * @return Scaling matrix
     */
    def scale(x: Float, y: Float, z: Float): Matrix4f = {
        this.multiply(Matrix4f.scale(x, y, z))
    }

}

object Matrix4f {

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
    def orthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4f = {
        val ortho = new Matrix4f
        ortho.m00 = 2.0f / (right - left)
        ortho.m11 = 2.0f / (top - bottom)
        ortho.m22 = 2.0f / (near - far)
        ortho.m03 = (left + right) / (left - right)
        ortho.m13 = (bottom + top) / (bottom - top)
        ortho.m23 = (far + near) / (far - near)
        ortho
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
    def frustum(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4f = {
        val frustum = new Matrix4f
        val a = (right + left) / (right - left)
        val b = (top + bottom) / (top - bottom)
        val c = -(far + near) / (far - near)
        val d = -(2f * far * near) / (far - near)
        frustum.m00 = (2f * near) / (right - left)
        frustum.m11 = (2f * near) / (top - bottom)
        frustum.m02 = a
        frustum.m12 = b
        frustum.m22 = c
        frustum.m32 = -1f
        frustum.m23 = d
        frustum.m33 = 0f
        frustum
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
    def perspective(fovy: Float, near: Float, far: Float): Matrix4f = {
        val result = new Matrix4f()
        val ar = 1.0f * Vals.WIDTH / Vals.HEIGHT
        val range = near - far
        val tanHalfFOV = Math.tan(Math.toRadians(fovy / 2)).toFloat

        result.m00 = 1.0f / tanHalfFOV / ar
        result.m11 = 1.0f / tanHalfFOV
        result.m22 = (-near - far) / range
        result.m23 = 2.0f * far * near / range
        result.m32 = 1f
        result.m33 = 0.0f

        result
    }

    /**
     * Creates a translation matrix. Similar to
     * <code>glTranslate(x, y, z)</code>.
     *
     * @param v: Vector to translate
     * @return Translation matrix
     */
    def translate(v: Vector3f): Matrix4f = translate(v.x, v.y, v.z)
    
    /**
     * Creates a translation matrix with rotation around the vertical y axis.
     *
     * @param x x coordinate of translation vector
     * @param y y coordinate of translation vector
     * @param z z coordinate of translation vector
     * @param a angle of rotation
     * @return Translation matrix
     */
    def place(x: Float, y: Float, z: Float, a: Float) = {
        translate(x, y, z).rotate(a, 0, 1, 0)
    }
    
    /**
     * Creates a translation matrix. Similar to
     * <code>glTranslate(x, y, z)</code>.
     *
     * @param x x coordinate of translation vector
     * @param y y coordinate of translation vector
     * @param z z coordinate of translation vector
     * @return Translation matrix
     */
    def translate(x: Float, y: Float, z: Float): Matrix4f = {
        val translation = new Matrix4f
        translation.m03 = x
        translation.m13 = y
        translation.m23 = z
        translation
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
    def rotate(angle: Float, nx: Float, ny: Float, nz: Float): Matrix4f = {
        val rotation = new Matrix4f
        val c = Math.cos(angle).toFloat
        val s = Math.sin(angle).toFloat
        val vec = new Vector3f(nx, ny, nz).normalize
        val x = vec.x
        val y = vec.y
        val z = vec.z
        rotation.m00 = x * x * (1f - c) + c
        rotation.m10 = y * x * (1f - c) + z * s
        rotation.m20 = x * z * (1f - c) - y * s
        rotation.m01 = x * y * (1f - c) - z * s
        rotation.m11 = y * y * (1f - c) + c
        rotation.m21 = y * z * (1f - c) + x * s
        rotation.m02 = x * z * (1f - c) + y * s
        rotation.m12 = y * z * (1f - c) - x * s
        rotation.m22 = z * z * (1f - c) + c
        rotation
    }

    /**
     * Creates a scaling matrix. Similar to <code>glScale(x, y, z)</code>.
     *
     * @param x Scale factor along the x coordinate
     * @param y Scale factor along the y coordinate
     * @param z Scale factor along the z coordinate
     * @return Scaling matrix
     */
    def scale(x: Float, y: Float, z: Float): Matrix4f = {
        val scaling = new Matrix4f
        scaling.m00 = x
        scaling.m11 = y
        scaling.m22 = z
        scaling
    }

}
