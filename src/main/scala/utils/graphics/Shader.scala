package utils.graphics

import java.nio.FloatBuffer

import utils.math._
import org.lwjgl.opengl.GL20._
import org.lwjgl.system.MemoryStack

import scala.collection.mutable

class Shader(programID: Int) {

    private val locationCache = mutable.Map.empty[String, Int]

    def getUniform(name: String): Int = locationCache.getOrElse(name, {
        val result = glGetUniformLocation(programID, name)
        if (result == -1) System.err.println("Could not find uniform variable '" + name + "'!")
        else locationCache.put(name, result)
        result
    })

    def loadUniformFloat(name: String, value: Float): Unit = glUniform1f(getUniform(name), value)
    def setUniform1fa(name: String, value: Array[Float]): Unit = glUniform1fv(getUniform(name), value)
    def loadUniformInt(name: String, value: Int): Unit = glUniform1i(getUniform(name), value)
    def loadUniformIntV(name: String, value: Array[Int]): Unit = glUniform1iv(getUniform(name), value)

    def loadUniformBoolean(name: String, value: Boolean): Unit = {
        var toLoad = 0
        if (value) toLoad = 1
        glUniform1f(getUniform(name), toLoad)
    }

    def loadUniformVec2f(name: String, value: Vec2): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2)
            value.toBuffer(buffer)
            glUniform2fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def setUniformVec2fa(name: String, value: Array[Vec2]): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2 * value.length)
            convertToBuffer(value, buffer)
            glUniform2fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def convertToBuffer(value: Array[Vec2], buffer: FloatBuffer) = {
        value.foreach(v => buffer.put(v.x).put(v.y))
        buffer.flip()
    }

    def loadUniformVec3f(name: String, value: Vec3): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(3)
            value.toBuffer(buffer)
            glUniform3fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformVec4f(name: String, value: Vec4): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(4)
            value.toBuffer(buffer)
            glUniform4fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def setUniformVec4fa(name: String, value: Array[Vec4]): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(4)
            convertToBuffer(value, buffer)
            glUniform4fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def convertToBuffer(value: Array[Vec4], buffer: FloatBuffer) = {
        value.foreach(v => buffer.put(v.x).put(v.y).put(v.z).put(v.w))
        buffer.flip()
    }

    def loadUniformMat2f(name: String, value: Mat2): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2 * 2)
            value.toBuffer(buffer)
            glUniformMatrix2fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformMat3f(name: String, value: Mat3): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(3 * 3)
            value.toBuffer(buffer)
            glUniformMatrix3fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformMat4f(name: String, value: Mat4): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(4 * 4)
            value.toBuffer(buffer)
            glUniformMatrix4fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    def bind(): Unit = glUseProgram(programID)
    def unbind(): Unit = glUseProgram(0)

}

object Shader {

    private val shaders = mutable.Map.empty[String, Shader]

    def get(shader: String): Shader = shaders.getOrElse(shader, throw new Error(s"Shader $shader not loaded"))
    def put(file: String, shader: Shader): Unit = shaders.put(file, shader)

}
