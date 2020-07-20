package utils.loader

import java.io.{BufferedReader, FileReader, IOException}

import utils.graphics.Mesh

import scala.collection.mutable

object OBJLoader {

    def loadModel(file: String): Mesh = {
        val vertices = new mutable.ArrayBuffer[Float]()
        val indices = new mutable.ArrayBuffer[Int]()
        try {
            val reader = new BufferedReader(new FileReader(s"src/main/resources/models/$file.obj"))

            var line = reader.readLine()
            do {
                val currentLine = line.split(" ")
                currentLine(0) match {
                    case "v" =>
                        vertices.addOne(currentLine(1).toFloat)
                        vertices.addOne(currentLine(2).toFloat)
                        vertices.addOne(currentLine(3).toFloat)
                    case "f" =>
                        indices.addOne(currentLine(1).toInt - 1)
                        indices.addOne(currentLine(3).toInt - 1)
                        indices.addOne(currentLine(2).toInt - 1)
                    case _ =>
                }
                line = reader.readLine()
            } while (line != null)
        } catch {
            case e: IOException =>
                e.printStackTrace()
                System.exit(-1)
        }

        new Mesh(vertices.toArray, indices.toArray, Array(3))
    }

}
