package utils.loader

import java.io.{FileInputStream, IOException}
import java.nio.{ByteBuffer, ByteOrder, IntBuffer}

import javax.imageio.ImageIO
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL45.glCreateTextures
import utils.graphics.Texture

object TextureLoader {

    def loadAll(): Unit = {
        load("logo")
        load("threeLane")
        load("cleanRoad")
    }

    private def load(file: String): Unit = {
        val texData = loadImage(s"src/main/resources/textures/$file.png")
        val result = glGenTextures()
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, result)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, texData._1, texData._2, 0, GL_RGBA, GL_UNSIGNED_BYTE, texData._3)
        glBindTexture(GL_TEXTURE_2D, 0)
        Texture.put(file, new Texture(texData._1, texData._2, result))
    }

    private val faces = Array("left", "right", "top", "bottom", "back", "front")

    def loadCubeMap(file: String): Texture = {
        val result = glCreateTextures(GL_TEXTURE_CUBE_MAP)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, result)

        var width = 0
        var height = 0
        for (i <- faces.indices) {
            val texData = loadImage(s"src/main/resources/skyboxes/$file/$file${faces(i)}.png")
            width = texData._1
            height = texData._2
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texData._3)
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0)

        new Texture(width, height, result)
    }

    private def loadImage(file: String): (Int, Int, IntBuffer) = {
        var width: Int = 0
        var height: Int = 0
        var pixels: Array[Int] = null
        try {
            val image = ImageIO.read(new FileInputStream(file))
            width = image.getWidth
            height = image.getHeight
            pixels = new Array[Int](width * height)
            image.getRGB(0, 0, width, height, pixels, 0, width)
        } catch {
            case e: IOException =>
                e.printStackTrace()
        }
        val data = new Array[Int](width * height)
        for (i <- 0 until width * height) {
            val a = (pixels(i) & 0xff000000) >> 24
            val r = (pixels(i) & 0xff0000) >> 16
            val g = (pixels(i) & 0xff00) >> 8
            val b = pixels(i) & 0xff
            data(i) = a << 24 | b << 16 | g << 8 | r
        }

        val buffer = ByteBuffer.allocateDirect(data.length << 2).order(ByteOrder.nativeOrder).asIntBuffer
        (width, height, buffer.put(data).flip)
    }

}
