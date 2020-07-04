package graphics

import java.io.{BufferedReader, FileReader, IOException}

import math.{Matrix2f, Matrix3f, Matrix4f, Vector2f, Vector3f, Vector4f}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.system.MemoryStack

import scala.collection.mutable

class Shader(programID: Int, vertID: Int, fragID: Int) {

    private val locationCache = mutable.Map.empty[String, Int]

    def getUniform(name: String): Int = locationCache.getOrElse(name, {
        val result = glGetUniformLocation(programID, name)
        if (result == -1) System.err.println("Could not find uniform variable '" + name + "'!")
        else locationCache.put(name, result)
        result
    })

    def loadUniformFloat(name: String, value: Float): Unit = glUniform1f(getUniform(name), value)
    def loadUniformInt(name: String, value: Int): Unit = glUniform1i(getUniform(name), value)

    def loadUniformBoolean(name: String, value: Boolean): Unit = {
        var toLoad = 0
        if (value) toLoad = 1
        glUniform1f(getUniform(name), toLoad)
    }

    def loadUniformVec2f(name: String, value: Vector2f): Unit = try {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2)
            value.toBuffer(buffer)
            glUniform2fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformVec3f(name: String, value: Vector3f): Unit = try {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(3)
            value.toBuffer(buffer)
            glUniform3fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformVec4f(name: String, value: Vector4f): Unit = try {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(4)
            value.toBuffer(buffer)
            glUniform4fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformMat2f(name: String, value: Matrix2f): Unit = try {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2 * 2)
            value.toBuffer(buffer)
            glUniformMatrix2fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformMat3f(name: String, value: Matrix3f): Unit = try {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(3 * 3)
            value.toBuffer(buffer)
            glUniformMatrix3fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformMat4f(name: String, value: Matrix4f): Unit = try {
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

    def get(shader: String): Shader = shaders.getOrElse(shader, throw new ShaderError(s"Shader $shader not loaded"))

    def loadShader(file: String): Unit = {
        val program = glCreateProgram()
        val shaderProgam = readShaderProgram(file)

        glAttachShader(program, shaderProgam._1)
        glAttachShader(program, shaderProgam._2)
        glLinkProgram(program)
        glValidateProgram(program)

        glDeleteShader(shaderProgam._1)
        glDeleteShader(shaderProgam._2)

        shaders.put(file, new Shader(program, shaderProgam._1, shaderProgam._2))
    }

    private def readShaderProgram(file: String): (Int, Int) = {
        val vertSource = new StringBuilder
        val fragSource = new StringBuilder
        try {
            val reader = new BufferedReader(new FileReader(s"src/main/resources/shaders/$file.shader"))
            if (!reader.readLine().contains("#vertex")) throw new ShaderError(s"Vertex shader not found in $file")
            var line = reader.readLine()
            do {
                vertSource.append(line).append("//\n")
                line = reader.readLine()
                if (line == null) throw new ShaderError(s"Fragment shader not found in $file")
            } while (!line.contains("#fragment"))
            line = reader.readLine()
            do {
                fragSource.append(line).append("//\n")
                line = reader.readLine()
            } while (line != null)
            reader.close()
        } catch {
            case e: IOException =>
                e.printStackTrace()
                System.exit(-1)
        }
        (compileShader(vertSource.result(), GL_VERTEX_SHADER), compileShader(fragSource.result(), GL_FRAGMENT_SHADER))
    }

    private def compileShader(source: String, shaderType: Int): Int = {
        val id = glCreateShader(shaderType)
        glShaderSource(id, source)
        glCompileShader(id)

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println(glGetShaderInfoLog(id, 500))
            println("Could not compile shader: ")
            System.exit(-1)
        }
        id
    }

    class ShaderError(msg: String) extends RuntimeException

}
