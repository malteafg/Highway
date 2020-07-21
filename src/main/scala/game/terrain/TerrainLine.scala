package game.terrain

import utils.math.{Vec2, Vec4}

class TerrainLine(var pos1: Vec2, var pos2: Vec2, width: Float, color: Vec4) {

    def updatePos(pos: Vec2) = pos2 = pos

    def setPos(pos: Vec2): Unit = {
        pos1 = pos
        pos2 = pos.add(new Vec2(0, 0.1f))
    }

    def getLine() = (pos1, pos2, width, color)

}
