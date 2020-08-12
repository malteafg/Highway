package game.cars

import game.roads.Lane
import utils.math.Vec3

case class Traveller(pos: Vec3, lane: Lane, distTravelledInPoint: Float, pointToGoTo: Int)