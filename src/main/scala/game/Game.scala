package game

import game.cars.Car
import game.roads.{RoadNode, RoadSegment}
import game.terrain.{Terrain, TerrainLine}

import scala.collection.mutable.ListBuffer

class Game {

    val terrain = new Terrain
    val spheres = new ListBuffer[Sphere]()

    val roads = new ListBuffer[RoadSegment]()
    val nodes = new ListBuffer[RoadNode]()
    var cars = new ListBuffer[Car]()

    def update(): Unit = {
        cars = cars.filterNot(c => c.update())
    }

    def addSegment(s: RoadSegment): Unit = roads.addOne(s)
    def getSegments: ListBuffer[RoadSegment] = roads
    def addNode(n: RoadNode): Unit = nodes.addOne(n)

    def addLineToTerrain(line: TerrainLine): Unit = terrain.addLine(line)

    def addCar(car: Car): Unit = cars.addOne(car)

}
