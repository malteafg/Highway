package game.roads

import utils.graphics.Mesh
import utils.math.Vector3f

case class RoadSegment(startPoint: SnapPoint, endPoint: SnapPoint, controlPoints: Array[Vector3f], mesh: Mesh) {

}

object RoadSegment {

    def generateStraightSegment(): RoadSegment = ???
    def generateCurvedSegment(): RoadSegment = ???

}
