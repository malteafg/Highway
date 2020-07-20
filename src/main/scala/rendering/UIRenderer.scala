package rendering

import java.nio.{ByteBuffer, ByteOrder}

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL45.{glCreateBuffers, glCreateTextures, glCreateVertexArrays, glEnableVertexArrayAttrib}
import ui.components.UIComponent
import utils.graphics.Shader
import utils.math.{Vector2f, Vector4f}

import scala.collection.mutable

object UIRenderer {

    final val maxQuadCount = 2
    final val maxVertexCount = maxQuadCount * 4
    final val maxIndexCount = maxQuadCount * 6
    final val maxTextures = 32
    final val vertexSize = 10

    var quadVA = 0
    var quadVB = 0
    var quadIB = 0

    val indices = new Array[Int](maxIndexCount)
    val textures = new Array[Int](maxTextures)

    val bufferData = new Array[Float](maxVertexCount * vertexSize)

    var quadCount = 0
    var texSlotIndex = 1

    def init(): Unit = {
        quadVA = glCreateVertexArrays()
        glBindVertexArray(quadVA)

        quadVB = glCreateBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, quadVB)
        //TODO fix 40, and calculate the size properly
        glBufferData(GL_ARRAY_BUFFER, maxVertexCount * 40, GL_DYNAMIC_DRAW)

        glEnableVertexArrayAttrib(quadVA, 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 40, 0)

        glEnableVertexArrayAttrib(quadVA, 1)
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 40, 12)

        glEnableVertexArrayAttrib(quadVA, 2)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 40, 28)

        glEnableVertexArrayAttrib(quadVA, 3)
        glVertexAttribPointer(3, 1, GL_FLOAT, false, 40, 36)

        var count = 0
        for(i <- 0 until maxIndexCount by 6) {
            indices(i + 0) = 0 + count
            indices(i + 1) = 1 + count
            indices(i + 2) = 2 + count
            indices(i + 3) = 2 + count
            indices(i + 4) = 3 + count
            indices(i + 5) = 0 + count

            count += 4
        }

        quadIB = glCreateBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quadIB)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glBindVertexArray(0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

        val whiteTex = glCreateTextures(GL_TEXTURE_2D)
        glBindTexture(GL_TEXTURE_2D, whiteTex)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        val buffer = ByteBuffer.allocateDirect(1 << 2).order(ByteOrder.nativeOrder).asIntBuffer
        buffer.put(Array(0xffffffff)).flip
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)

        textures(0) = whiteTex
        for(i <- 1 until maxTextures) textures(i) = 0

        val samplers = new Array[Int](32)
        for (i <- 0 until 32) samplers(i) = i
        Shader.get("UI").bind()
        Shader.get("UI").loadUniformIntV("u_Textures", samplers)
    }

    def flush(): Unit = {
        glBindBuffer(GL_ARRAY_BUFFER, quadVB)
        glBufferSubData(GL_ARRAY_BUFFER, 0, bufferData)

        glBindVertexArray(quadVA)
        glDrawElements(GL_TRIANGLES, quadCount * 6, GL_UNSIGNED_INT, 0)

        glBindVertexArray(0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

        quadCount = 0
    }

    def drawQuad(pos: Vector2f, size: Vector2f, color: Vector4f): Unit = {
        if (quadCount >= maxQuadCount) flush()

        bufferData(quadCount * vertexSize * 4 + 0) = pos.x
        bufferData(quadCount * vertexSize * 4 + 1) = pos.y
        bufferData(quadCount * vertexSize * 4 + 2) = 0
        bufferData(quadCount * vertexSize * 4 + 3) = color.x
        bufferData(quadCount * vertexSize * 4 + 4) = color.y
        bufferData(quadCount * vertexSize * 4 + 5) = color.z
        bufferData(quadCount * vertexSize * 4 + 6) = color.w
        bufferData(quadCount * vertexSize * 4 + 7) = 0.0f
        bufferData(quadCount * vertexSize * 4 + 8) = 0.0f
        bufferData(quadCount * vertexSize * 4 + 9) = 0.0f

        bufferData(quadCount * vertexSize * 4 + 10) = pos.x + size.x
        bufferData(quadCount * vertexSize * 4 + 11) = pos.y
        bufferData(quadCount * vertexSize * 4 + 12) = 0
        bufferData(quadCount * vertexSize * 4 + 13) = color.x
        bufferData(quadCount * vertexSize * 4 + 14) = color.y
        bufferData(quadCount * vertexSize * 4 + 15) = color.z
        bufferData(quadCount * vertexSize * 4 + 16) = color.w
        bufferData(quadCount * vertexSize * 4 + 17) = 1.0f
        bufferData(quadCount * vertexSize * 4 + 18) = 0.0f
        bufferData(quadCount * vertexSize * 4 + 19) = 0.0f

        bufferData(quadCount * vertexSize * 4 + 20) = pos.x + size.x
        bufferData(quadCount * vertexSize * 4 + 21) = pos.y + size.y
        bufferData(quadCount * vertexSize * 4 + 22) = 0
        bufferData(quadCount * vertexSize * 4 + 23) = color.x
        bufferData(quadCount * vertexSize * 4 + 24) = color.y
        bufferData(quadCount * vertexSize * 4 + 25) = color.z
        bufferData(quadCount * vertexSize * 4 + 26) = color.w
        bufferData(quadCount * vertexSize * 4 + 27) = 1.0f
        bufferData(quadCount * vertexSize * 4 + 28) = 1.0f
        bufferData(quadCount * vertexSize * 4 + 29) = 0.0f

        bufferData(quadCount * vertexSize * 4 + 30) = pos.x
        bufferData(quadCount * vertexSize * 4 + 31) = pos.y + size.y
        bufferData(quadCount * vertexSize * 4 + 32) = 0
        bufferData(quadCount * vertexSize * 4 + 33) = color.x
        bufferData(quadCount * vertexSize * 4 + 34) = color.y
        bufferData(quadCount * vertexSize * 4 + 35) = color.z
        bufferData(quadCount * vertexSize * 4 + 36) = color.w
        bufferData(quadCount * vertexSize * 4 + 37) = 0.0f
        bufferData(quadCount * vertexSize * 4 + 38) = 1.0f
        bufferData(quadCount * vertexSize * 4 + 39) = 0.0f

        quadCount += 1
    }

    def drawQuad(pos: Vector2f, size: Vector2f, textureID: Int): Unit = {
        if (quadCount >= maxQuadCount || texSlotIndex >= maxTextures) flush()

        var texIndex = 0.0f
        for (i <- 1 until texSlotIndex) if (textures(i) == textureID) texIndex = i

        if (texIndex == 0.0f) {
            texIndex = texSlotIndex
            textures(texSlotIndex) = textureID
            texSlotIndex += 1

            glActiveTexture(texIndex.toInt)
            glBindTexture(GL_TEXTURE_2D, textureID)
        }

        val color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f)

        bufferData(quadCount * vertexSize * 4 + 0) = pos.x
        bufferData(quadCount * vertexSize * 4 + 1) = pos.y
        bufferData(quadCount * vertexSize * 4 + 2) = 0
        bufferData(quadCount * vertexSize * 4 + 3) = color.x
        bufferData(quadCount * vertexSize * 4 + 4) = color.y
        bufferData(quadCount * vertexSize * 4 + 5) = color.z
        bufferData(quadCount * vertexSize * 4 + 6) = color.w
        bufferData(quadCount * vertexSize * 4 + 7) = 0.0f
        bufferData(quadCount * vertexSize * 4 + 8) = 0.0f
        bufferData(quadCount * vertexSize * 4 + 9) = texIndex

        bufferData(quadCount * vertexSize * 4 + 10) = pos.x + size.x
        bufferData(quadCount * vertexSize * 4 + 11) = pos.y
        bufferData(quadCount * vertexSize * 4 + 12) = 0
        bufferData(quadCount * vertexSize * 4 + 13) = color.x
        bufferData(quadCount * vertexSize * 4 + 14) = color.y
        bufferData(quadCount * vertexSize * 4 + 15) = color.z
        bufferData(quadCount * vertexSize * 4 + 16) = color.w
        bufferData(quadCount * vertexSize * 4 + 17) = 1.0f
        bufferData(quadCount * vertexSize * 4 + 18) = 0.0f
        bufferData(quadCount * vertexSize * 4 + 19) = texIndex

        bufferData(quadCount * vertexSize * 4 + 20) = pos.x + size.x
        bufferData(quadCount * vertexSize * 4 + 21) = pos.y + size.y
        bufferData(quadCount * vertexSize * 4 + 22) = 0
        bufferData(quadCount * vertexSize * 4 + 23) = color.x
        bufferData(quadCount * vertexSize * 4 + 24) = color.y
        bufferData(quadCount * vertexSize * 4 + 25) = color.z
        bufferData(quadCount * vertexSize * 4 + 26) = color.w
        bufferData(quadCount * vertexSize * 4 + 27) = 1.0f
        bufferData(quadCount * vertexSize * 4 + 28) = 1.0f
        bufferData(quadCount * vertexSize * 4 + 29) = texIndex

        bufferData(quadCount * vertexSize * 4 + 30) = pos.x
        bufferData(quadCount * vertexSize * 4 + 31) = pos.y + size.y
        bufferData(quadCount * vertexSize * 4 + 32) = 0
        bufferData(quadCount * vertexSize * 4 + 33) = color.x
        bufferData(quadCount * vertexSize * 4 + 34) = color.y
        bufferData(quadCount * vertexSize * 4 + 35) = color.z
        bufferData(quadCount * vertexSize * 4 + 36) = color.w
        bufferData(quadCount * vertexSize * 4 + 37) = 0.0f
        bufferData(quadCount * vertexSize * 4 + 38) = 1.0f
        bufferData(quadCount * vertexSize * 4 + 39) = texIndex

        quadCount += 1
    }

    def render(screen: UIComponent) = {
        glDisable(GL_DEPTH_TEST)

        Shader.get("UI").bind()

        val elements = new mutable.Queue[UIComponent]()
        elements.enqueue(screen)
        while (!elements.isEmpty) {
            val e = elements.dequeue()
            if (e.isActive()) {
                if (e.tex == null) UIRenderer.drawQuad(e.getPos, e.getSize, e.getColor)
                else UIRenderer.drawQuad(e.getPos, e.getSize, e.tex.getTextureID)
                elements.enqueueAll(e.getChildren())
            }
        }
        UIRenderer.flush()
        glEnable(GL_DEPTH_TEST)
    }

}
