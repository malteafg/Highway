package game

import game.roads.RoadSegment
import game.terrain.{TerrainLine, Terrain}
import utils.math.{Vector2f, Vector4f}

import scala.collection.mutable.ListBuffer

class Game {

    val terrain = new Terrain
    terrain.lines.addOne(new TerrainLine(new Vector2f(10, 0), new Vector2f(10, 5), 1, new Vector4f(1.0f, 0.0f, 0.2f, 0.5f)))
    val spheres = new ListBuffer[Sphere]()
    val roads = new ListBuffer[RoadSegment]()

}
