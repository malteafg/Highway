package game.terrain

import utils.Vals
import utils.math.{Vec2, Vec4}

class TerrainLine(var pos1: Vec2 = Vec2(), var pos2: Vec2 = Vec2(), var width: Float = 0.5f, var color: Vec4 = Vals.defaultTerrainLineColor) {

    /**
     * Setters
     */
    def setPos(pos1: Vec2 = this.pos1, pos2: Vec2 = this.pos2): Unit = {
        this.pos1 = pos1
        this.pos2 = pos2
    }

    def setPosAsPoint(pos: Vec2): Unit = {
        pos1 = pos
        pos2 = pos.add(Vec2(0, 0.01f))
    }

    def setWidth(width: Float): Unit = this.width = width

    def setColor(color: Vec4): Unit = this.color = color

    /**
     * Getters
     */
    def getPos1: Vec2 = pos1
    def getPos2: Vec2 = pos2
    def getWidth: Float = width
    def getColor: Vec4 = color

}
