package game

import utils.loader.OBJLoader
import utils.math.{Mat4, Vec3, Vec4}

class Sphere(var position: Vec3, var color: Vec4) {

   def this(pos: Vec3) {
       this(pos, new Vec4(0.9f, 0, 0, 1))
   }


}

object Sphere {

    val mesh = OBJLoader.loadModel("sphere")

}
