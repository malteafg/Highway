package utils.math

object VecUtils {

    def convertToFloatArray(array: Array[Vec3]): Array[Float] = {
        val floats = new Array[Float](array.length * 3)
        for(i <- array.indices) {
            floats(i * 3 + 0) = array(i).x
            floats(i * 3 + 1) = array(i).y
            floats(i * 3 + 2) = array(i).z
        }
        floats
    }

}
