package utils.graphics

import java.io.{BufferedReader, FileReader, IOException}
import java.nio.FloatBuffer

import utils.math._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL32._
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

    def loadUniformVec2f(name: String, value: Vector2f): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2)
            value.toBuffer(buffer)
            glUniform2fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def setUniformVec2fa(name: String, value: Array[Vector2f]): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2 * value.length)
            convertToBuffer(value, buffer)
            glUniform2fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def convertToBuffer(value: Array[Vector2f], buffer: FloatBuffer) = {
        value.foreach(v => buffer.put(v.x).put(v.y))
        buffer.flip()
    }

    def loadUniformVec3f(name: String, value: Vector3f): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(3)
            value.toBuffer(buffer)
            glUniform3fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformVec4f(name: String, value: Vector4f): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(4)
            value.toBuffer(buffer)
            glUniform4fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def setUniformVec4fa(name: String, value: Array[Vector4f]): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(4)
            convertToBuffer(value, buffer)
            glUniform4fv(getUniform(name), buffer)
        } finally if (stack != null) stack.close()
    }

    def convertToBuffer(value: Array[Vector4f], buffer: FloatBuffer) = {
        value.foreach(v => buffer.put(v.x).put(v.y).put(v.z).put(v.w))
        buffer.flip()
    }

    def loadUniformMat2f(name: String, value: Matrix2f): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(2 * 2)
            value.toBuffer(buffer)
            glUniformMatrix2fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformMat3f(name: String, value: Matrix3f): Unit = {
        val stack = MemoryStack.stackPush
        try {
            val buffer = stack.mallocFloat(3 * 3)
            value.toBuffer(buffer)
            glUniformMatrix3fv(getUniform(name), false, buffer)
        } finally if (stack != null) stack.close()
    }

    def loadUniformMat4f(name: String, value: Matrix4f): Unit = {
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

    def loadShader(file: String, projMatrix: Matrix4f = null): Unit = {
        val program = glCreateProgram()
        val shaderProgam = readShaderProgram(file)

        glAttachShader(program, shaderProgam._1)
        if (shaderProgam._2 != -1) glAttachShader(program, shaderProgam._2)
        glAttachShader(program, shaderProgam._3)
        glLinkProgram(program)
        glValidateProgram(program)

        glDeleteShader(shaderProgam._1)
        if (shaderProgam._2 != -1) glDeleteShader(shaderProgam._2)
        glDeleteShader(shaderProgam._3)

        val shader = new Shader(program)
        shaders.put(file, shader)
        if(projMatrix != null) {
            shader.bind()
            shader.loadUniformMat4f("projMatrix", projMatrix)
        }
    }

    private def readShaderProgram(file: String): (Int, Int, Int) = {
        val vertSource = new StringBuilder
        val geomSource = new StringBuilder
        val fragSource = new StringBuilder
        try {
            val reader = new BufferedReader(new FileReader(s"src/main/resources/shaders/$file.shader"))
            if (!reader.readLine().contains("#vertex")) throw new ShaderError(s"vertex shader not found in $file")
            var line = readShader(vertSource, reader, "#vertex")
            if (line.contains("#geometry")) line = readShader(geomSource, reader, "#geometry")
            if (!line.contains("#fragment")) throw new ShaderError(s"fragment shader not found in $file")
            readShader(fragSource, reader, "#fragment")
            reader.close()
        } catch {
            case e: IOException =>
                e.printStackTrace()
                System.exit(-1)
        }
        (compileShader(vertSource.result(), GL_VERTEX_SHADER), if (geomSource.isEmpty) -1 else compileShader(geomSource.result(), GL_GEOMETRY_SHADER), compileShader(fragSource.result(), GL_FRAGMENT_SHADER))
    }

    private def readShader(source: mutable.StringBuilder, reader: BufferedReader, shader: String): String = {
        var line = reader.readLine()
        do {
            source.append(line).append("//\n")
            line = reader.readLine()
        } while (line != null && !line.contains("#vertex") && !line.contains("#geometry") && !line.contains("#fragment"))
        line
    }

    private def compileShader(source: String, shaderType: Int): Int = {
        val id = glCreateShader(shaderType)
        glShaderSource(id, source)
        glCompileShader(id)

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println(glGetShaderInfoLog(id, 500))
            println(s"Could not compile shader: $source")
            System.exit(-1)
        }
        id
    }

    class ShaderError(msg: String) extends RuntimeException

}
