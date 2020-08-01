package game.terrain

import utils.Vals
import utils.math.{Vec2, Vec4}

class TerrainLine(
                     var pos1: () => Vec2, var pos2: () => Vec2 = () => Vec2(),
                     var width: () => Float = () => 0.5f, var color: Vec4 = Vals.defaultTerrainLineColor) {

    /**
     * Getters
     */
    def getPos1: Vec2 = pos1()
    def getPos2: Vec2 = pos2()
    def getWidth: Float = width()
    def getColor: Vec4 = color

}
