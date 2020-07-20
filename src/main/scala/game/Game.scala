package game

import game.roads.{RoadNode, RoadSegment}
import game.terrain.{Terrain, TerrainLine}
import utils.graphics.Mesh
import utils.math.{Vector2f, Vector4f}

import scala.collection.mutable.ListBuffer

class Game {

    val terrain = new Terrain
    terrain.lines.addOne(new TerrainLine(new Vector2f(10, 0), new Vector2f(10, 5), 2, new Vector4f(1.0f, 0.0f, 0.2f, 0.8f)))
    val spheres = new ListBuffer[Sphere]()

    val roads = new ListBuffer[RoadSegment]()
    val nodes = new ListBuffer[RoadNode]()

    def buildRoad(startNode: RoadNode, endNode: RoadNode, mesh: Mesh): Unit = {
        if (!nodes.contains(startNode)) nodes.addOne(startNode)
        nodes.addOne(endNode)
        roads.addOne(new RoadSegment(startNode, endNode, mesh))
    }

}
