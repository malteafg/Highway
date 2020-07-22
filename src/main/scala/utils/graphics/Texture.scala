package utils.graphics

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._

import scala.collection.mutable

class Texture(width: Int, height: Int, textureID: Int) {

    def bind(slot: Int = 0): Unit = {
        glActiveTexture(GL_TEXTURE0 + slot)
        glBindTexture(GL_TEXTURE_2D, textureID)
    }

    def unbind(): Unit = glBindTexture(GL_TEXTURE_2D, 0)

    def getTextureID: Int = textureID

}

object Texture {

    private val textures = mutable.Map.empty[String, Texture]

    def get(texture: String): Texture = textures.getOrElse(texture, throw new Error(s"Texture $texture not loaded"))
    def put(file: String, texture: Texture): Unit = textures.put(file, texture)

}
