package rendering

import utils.graphics.{Mesh, Shader, Texture}
import utils.loader.TextureLoader

class Skybox {

    final val size = 5000f

    val texture: Texture = TextureLoader.loadCubeMap("clouds")
    val mesh: Mesh = Mesh.generateCube(size)
    val shader: Shader = Shader.get("skybox")

}
