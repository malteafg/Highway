package game.roads

import utils.math.Vec3

import scala.collection.mutable

class LaneNode(var pos: Vec3, var dir: Vec3) {

    private val incomingLanes = new mutable.ListBuffer[Lane]
    private val outgoingLanes = new mutable.ListBuffer[Lane]

    def addIncomingLane(lane: Lane): Unit = incomingLanes.addOne(lane)
    def addOutgoingLane(lane: Lane): Unit = outgoingLanes.addOne(lane)

    def getIncomingLanes: mutable.ListBuffer[Lane] = incomingLanes
    def getOutgoingLanes: mutable.ListBuffer[Lane] = outgoingLanes

    def map: (Boolean, Boolean) = (outgoingLanes.isEmpty, incomingLanes.isEmpty)

}
