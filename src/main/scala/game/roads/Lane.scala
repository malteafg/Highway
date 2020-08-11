package game.roads

import utils.math.Vec3

class Lane(startNode: LaneNode, endNode: LaneNode, path: Array[Vec3]) {

    startNode.addOutgoingLane(this)
    endNode.addIncomingLane(this)

    def getPath: Array[Vec3] = path

}
