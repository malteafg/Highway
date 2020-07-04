package graphics

import java.io.{FileInputStream, IOException}
import java.nio.{ByteBuffer, ByteOrder}

import javax.imageio.ImageIO
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL13._

class Texture(path: String) {

    private var width = 0
    private var height = 0
    private val textureID = load(path)

    private def load(path: String) = {
        var pixels: Array[Int] = null
        try {
            val image = ImageIO.read(new FileInputStream(s"src/main/resources/textures/$path.png"))
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
        val result = glGenTextures
        glBindTexture(GL_TEXTURE_2D, result)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        val buffer = ByteBuffer.allocateDirect(data.length << 2).order(ByteOrder.nativeOrder).asIntBuffer
        buffer.put(data).flip
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
        glBindTexture(GL_TEXTURE_2D, 0)
        result
    }

    def bind(slot: Int = 0): Unit = {
        glActiveTexture(GL_TEXTURE0 + slot)
        glBindTexture(GL_TEXTURE_2D, textureID)
    }

    def unbind(): Unit = glBindTexture(GL_TEXTURE_2D, 0)

    def getTextureID: Int = textureID

}
