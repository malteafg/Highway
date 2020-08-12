package game.cars

import utils.math.Vec3

class Car(var traveller: Traveller, speed: Float) {

    private val metersPerFrame = speed * 1000 / 60 / 60 / 60

    def update(): Boolean = {
        traveller = traveller.lane.travel(traveller, metersPerFrame)
        if (traveller == null) true
        else false
    }

    def getPos: Vec3 = traveller.pos

}
