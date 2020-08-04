package game.roads

import utils.graphics.Mesh
import utils.math.Vec3

case class RoadSegment(startNode: RoadNode, endNode: RoadNode, lanes: Array[Lane], controlPoints: Array[Vec3], var mesh: Mesh) {

    def updateMesh(newMesh: Mesh): Unit = {
        mesh.delete()
        mesh = newMesh
    }
    def getMesh: Mesh = mesh

}