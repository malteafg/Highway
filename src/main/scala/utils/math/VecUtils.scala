package utils.math

object VecUtils {

    def toFloatArray(array: Array[Vec2]): Array[Float] = {
        val floats = new Array[Float](array.length * 2)
        for(i <- array.indices) {
            floats(i * 2 + 0) = array(i).x
            floats(i * 2 + 1) = array(i).y
        }
        floats
    }

    def toFloatArray(array: Array[Vec3]): Array[Float] = {
        val floats = new Array[Float](array.length * 3)
        for(i <- array.indices) {
            floats(i * 3 + 0) = array(i).x
            floats(i * 3 + 1) = array(i).y
            floats(i * 3 + 2) = array(i).z
        }
        floats
    }

    def toFloatArray(array: Array[Vec4]): Array[Float] = {
        val floats = new Array[Float](array.length * 4)
        for(i <- array.indices) {
            floats(i * 4 + 0) = array(i).x
            floats(i * 4 + 1) = array(i).y
            floats(i * 4 + 2) = array(i).z
            floats(i * 4 + 3) = array(i).w
        }
        floats
    }

}
