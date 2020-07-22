package utils.graphics

import org.lwjgl.opengl.GL15.{GL_DYNAMIC_DRAW, glBindBuffer, glBufferData, glBufferSubData}
import utils.math._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30.glBindBufferBase
import org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER
import org.lwjgl.opengl.GL45._
import org.lwjgl.system.MemoryStack

import scala.collection.mutable

class Shader(programID: Int) {

    private val locationCache = mutable.Map.empty[String, Int]

    // Int is binding. (Int, Int) is (bufferID, currentSize)
    private val shaderStorageBufferCache = mutable.Map.empty[Int, (Int, Int)]

    def getUniform(name: String): Int = locationCache.getOrElse(name, {
        val result = glGetUniformLocation(programID, name)
        if (result == -1) System.err.println("Could not find uniform variable '" + name + "'!")
        else locationCache.put(name, result)
        result
    })

    /**
     * Set uniforms
     */
    def uniform1f(name: String, value: Float): Unit = glUniform1f(getUniform(name), value)
    def uniform1fv(name: String, value: Array[Float]): Unit = glUniform1fv(getUniform(name), value)
    def uniform1i(name: String, value: Int): Unit = glUniform1i(getUniform(name), value)
    def uniform1iv(name: String, value: Array[Int]): Unit = glUniform1iv(getUniform(name), value)
    def uniform1b(name: String, value: Boolean): Unit =  glUniform1f(getUniform(name), if (value) 1 else 0)

    /**
     * Vectors
     */
    def uniformVec2f(name: String, value: Vec2): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2)
            value.toBuffer(buffer)
            glUniform2fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def uniformVec3f(name: String, value: Vec3): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(3)
            value.toBuffer(buffer)
            glUniform3fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def uniformVec4f(name: String, value: Vec4): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(4)
            value.toBuffer(buffer)
            glUniform4fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    /**
     * Matrices
     */
    def uniformMat2f(name: String, value: Mat2): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2 * 2)
            value.toBuffer(buffer)
            glUniformMatrix2fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    def uniformMat3f(name: String, value: Mat3): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(3 * 3)
            value.toBuffer(buffer)
            glUniformMatrix3fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    def uniformMat4f(name: String, value: Mat4): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(4 * 4)
            value.toBuffer(buffer)
            glUniformMatrix4fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    /**
     * Storage Buffers
     */
    def createStorageBuffer(binding: Int): Unit = {
        val id = glCreateBuffers()
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id)
        glBufferData(GL_SHADER_STORAGE_BUFFER, 100, GL_DYNAMIC_DRAW)
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, binding, id)
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0)
        shaderStorageBufferCache.put(binding, (id, 100))
    }

    def updateStorageBuffer(binding: Int, data: Array[Float]): Unit = {
        val buffer = shaderStorageBufferCache.getOrElse(binding, throw new Error(s"$binding does not exist in shader"))
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer._1)
        if (data.length > buffer._2) {
            glBufferData(GL_SHADER_STORAGE_BUFFER, data.length, GL_DYNAMIC_DRAW)
            shaderStorageBufferCache.update(binding, (buffer._1, data.length))
        }
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, data)
    }

    def bind(): Unit = glUseProgram(programID)
    def unbind(): Unit = glUseProgram(0)

}

object Shader {

    private val shaders = mutable.Map.empty[String, Shader]

    def get(shader: String): Shader = shaders.getOrElse(shader, throw new Error(s"Shader $shader not loaded"))
    def put(file: String, shader: Shader): Unit = shaders.put(file, shader)

}
