package game

import game.roads.{Lane, RoadNode, RoadSegment}
import game.terrain.{Terrain, TerrainLine}
import utils.graphics.Mesh

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Game {

    val terrain = new Terrain
    val spheres = new ListBuffer[Sphere]()

    val roads = new ListBuffer[RoadSegment]()
    val nodes = new ListBuffer[RoadNode]()

    def buildRoad(startNode: RoadNode, endNode: RoadNode, mesh: Mesh): Unit = {
        if (!nodes.contains(startNode)) nodes.addOne(startNode)
        nodes.addOne(endNode)
        roads.addOne(new RoadSegment(startNode, endNode, new mutable.ListBuffer[Lane](), mesh))
    }

    def addLineToTerrain(line: TerrainLine): Unit = terrain.addLine(line)

}
