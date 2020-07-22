package game

import game.roads.{RoadNode, RoadSegment}
import game.terrain.{Terrain, TerrainLine}
import utils.graphics.Mesh
import utils.math.{Vec2, Vec4}

import scala.collection.mutable.ListBuffer

class Game {

    val terrain = new Terrain
    //terrain.lines.addOne(new TerrainLine(Vec2(10, 0), Vec2(10, 5), 2, Vec4(1.0f, 0.0f, 0.2f, 0.8f)))
    val spheres = new ListBuffer[Sphere]()

    val roads = new ListBuffer[RoadSegment]()
    val nodes = new ListBuffer[RoadNode]()

    def buildRoad(startNode: RoadNode, endNode: RoadNode, mesh: Mesh): Unit = {
        if (!nodes.contains(startNode)) nodes.addOne(startNode)
        nodes.addOne(endNode)
        roads.addOne(new RoadSegment(startNode, endNode, mesh))
    }

}
