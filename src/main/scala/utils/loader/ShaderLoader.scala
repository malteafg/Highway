package utils.loader

import java.io.{BufferedReader, FileReader, IOException}

import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL20.{GL_COMPILE_STATUS, GL_FRAGMENT_SHADER, GL_VERTEX_SHADER, glAttachShader, glCompileShader, glCreateProgram, glCreateShader, glDeleteShader, glGetShaderInfoLog, glGetShaderi, glLinkProgram, glShaderSource, glValidateProgram}
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import utils.Vals
import utils.graphics.Shader
import utils.math.Mat4

import scala.collection.mutable

object ShaderLoader {

    def loadAll(): Unit = {
        loadShader("UI", Vals.UIProjMatrix)
        loadShader("sphere", Vals.perspectiveMatrix)
        loadShader("road", Vals.perspectiveMatrix)
        loadShader("skybox", Vals.perspectiveMatrix)

        val terrain = loadShader("terrain", Vals.perspectiveMatrix)
        terrain.createStorageBuffer(2)
        terrain.createStorageBuffer(3)
        terrain.createStorageBuffer(4)
        terrain.createStorageBuffer(5)
    }

    private def loadShader(file: String, projMatrix: Mat4 = null): Shader = {
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
        Shader.put(file, shader)
        if(projMatrix != null) {
            shader.bind()
            shader.uniformMat4f("projMatrix", projMatrix)
        }
        shader
    }

    private def readShaderProgram(file: String): (Int, Int, Int) = {
        val vertSource = new StringBuilder
        val geomSource = new StringBuilder
        val fragSource = new StringBuilder
        try {
            val reader = new BufferedReader(new FileReader(s"src/main/resources/shaders/$file.shader"))
            if (!reader.readLine().contains("#vertex")) throw new Error(s"vertex shader not found in $file")
            var line = readShader(vertSource, reader, "#vertex")
            if (line.contains("#geometry")) line = readShader(geomSource, reader, "#geometry")
            if (!line.contains("#fragment")) throw new Error(s"fragment shader not found in $file")
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
            throw new Error(s"Could not compile shader: $source")
        }
        id
    }

}
