package game

import utils.loader.OBJLoader
import utils.math.{Matrix4f, Vector3f}

class Sphere(var position: Vector3f) {



}

object Sphere {

    val mesh = OBJLoader.loadModel("sphere")

}
