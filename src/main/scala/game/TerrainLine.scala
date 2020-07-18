package game

import utils.math.{Vector2f, Vector4f}

class TerrainLine(pos1: Vector2f, var pos2: Vector2f, width: Float, color: Vector4f) {

    def updatePos(pos: Vector2f) = pos2 = pos

    def getLine() = (pos1, pos2, width, color)

}
