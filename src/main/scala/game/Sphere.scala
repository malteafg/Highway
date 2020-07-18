package game

import utils.loader.OBJLoader
import utils.math.{Matrix4f, Vector3f, Vector4f}

class Sphere(var position: Vector3f, var color: Vector4f) {

   def this(pos: Vector3f) {
       this(pos, new Vector4f(0.9f, 0, 0, 1))
   }


}

object Sphere {

    val mesh = OBJLoader.loadModel("sphere")

}
