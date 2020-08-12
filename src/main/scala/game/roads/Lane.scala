package game.roads

import game.cars.Traveller
import utils.math.Vec3

class Lane(startNode: LaneNode, endNode: LaneNode, path: Array[Vec3]) {

    startNode.addOutgoingLane(this)
    endNode.addIncomingLane(this)

    def travel(traveller: Traveller, dist: Float): Traveller = {
        val i = traveller.pointToGoTo
        val distToPoint = path(i).subtract(path(i - 1)).length - traveller.distTravelledInPoint
        if (distToPoint > dist) {
            Traveller(path(i).subtract(path(i).subtract(path(i - 1)).normalize.scale(distToPoint)), this, traveller.distTravelledInPoint + dist, i)
        } else {
            if (i == path.length - 1) {
                if (endNode.getOutgoingLanes.isEmpty) null
                else endNode.getOutgoingLanes.head.travel(Traveller(endNode.pos, endNode.getOutgoingLanes.head, 0, 1), dist - distToPoint)
            } else this.travel(Traveller(path(i + 1), this, 0, i + 1), dist - distToPoint)
        }
    }

    def getPath: Array[Vec3] = path

}
