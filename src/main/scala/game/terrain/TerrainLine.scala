package game.terrain

import utils.math.{Vector2f, Vector4f}

class TerrainLine(var pos1: Vector2f, var pos2: Vector2f, width: Float, color: Vector4f) {

    def updatePos(pos: Vector2f) = pos2 = pos

    def setPos(pos: Vector2f): Unit = {
        pos1 = pos
        pos2 = pos.add(new Vector2f(0, 0.1f))
    }

    def getLine() = (pos1, pos2, width, color)

}
