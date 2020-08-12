package game.tools

import game.Game
import game.cars.{Car, Traveller}
import game.terrain.TerrainLine
import game.tools.NodeSnapper.SnapPoint
import input.{InputEvent, InputHandler, Keys, Mouse}
import org.lwjgl.opengl.GL11
import utils.{Options, Vals}
import utils.math.{Vec2, Vec3, Vec4}

import scala.collection.mutable

object Tools {

    sealed trait Mode
    case object Free extends Mode
    case object Straight extends Mode
    case object Curved extends Mode

    private var mode: Mode = Free
    private var noOfLanes = 3

    private val stack = mutable.Stack[State]()
    private var game: Game = _

    val roadWidth: () => Float = () => noOfLanes * Vals.LARGE_LANE_WIDTH
    var roadMode: Int = GL11.GL_TRIANGLES

    private var allowedPos = Vec3()
    private var cursorPos = Vec3()
    private val cursorMarker = new TerrainLine(() => getAllowedPos.xz, () => getAllowedPos.xz.add(Vec2(0.01f)), width = roadWidth, color = Vec4(0.0f, 0.6f, 0.9f, 0.8f))

    def init(game: Game): Unit = {
        this.game = game
        NodeSnapper.init(game.nodes)
    }

    /**
     * Functions
     */
    def straightRoad(): Unit = {
        if (stack.isEmpty) push(SelectPos())
        var callswitch = false
        if (mode != Straight) callswitch = true
        mode = Straight
        if (callswitch) current.onModeSwitch(Straight)
    }

    def curvedRoad(): Unit = {
        if (stack.isEmpty) push(SelectPos())
        var callswitch = false
        if (mode != Curved) callswitch = true
        mode = Curved
        if (callswitch) current.onModeSwitch(Curved)
    }

    def freeMode(): Unit = {
        mode = Free
        clearStack()
    }

    def updateAllowedPos(pos: Vec3): Unit = allowedPos = pos

    /**
     * Stack
     */
    def clearStack(): Unit = stack.clear()

    def resetStack(): Unit = {
        clearStack()
        push(SelectPos())
    }

    def current: State = try stack.top catch {
        case _: IndexOutOfBoundsException | _: NoSuchElementException => null
    }

    def push(state: State): Unit = {
        stack.push(state)
        Options.log(s"State pushed on tool stack: $state \n  $stack\n", Options.State)
    }

    def back(): Unit = {
        val state = current
        stack.pop()
        Options.log(s"State removed from tool stack: $state \n  $stack\n", Options.State)
    }

    def replace(state: State): Unit = {
        back()
        push(state)
    }

    /**
     * Getters
     */
    def getMode: Mode = mode
    def isFree: Boolean = mode == Tools.Free
    def getAllowedPos: Vec3 = allowedPos
    def getCursorPos: Vec3 = cursorPos
    def getCursorMarker: TerrainLine = cursorMarker
    def getNoOfLanes: Int = noOfLanes
    def getGame: Game = game
    def building: Boolean = current match {
        case Preview(_, _, _, SnapPoint(_, _, _, _, _)) | SnapCurve(_, _, _, _) => true
        case _ => false
    }

    /**
     * Input
     */
    def onKeyPress(e: InputEvent): Unit = e match {
        case InputEvent(49, Keys.PRESSED, _) => switchNoOfLanes(1)
        case InputEvent(50, Keys.PRESSED, _) => switchNoOfLanes(2)
        case InputEvent(51, Keys.PRESSED, _) => switchNoOfLanes(3)
        case InputEvent(52, Keys.PRESSED, _) => switchNoOfLanes(4)
        case InputEvent(88, Keys.PRESSED, _) => straightRoad()
        case InputEvent(67, Keys.PRESSED, _) => curvedRoad()
        case InputEvent(75, Keys.PRESSED, _) => roadMode = GL11.GL_TRIANGLES
        case InputEvent(76, Keys.PRESSED, _) => roadMode = GL11.GL_LINES
        case _ =>
    }

    def onMousePress(e: InputEvent): Unit = e match {
        case InputEvent(Mouse.LEFT, Mouse.PRESSED, _) =>
            if (mode != Free) current.onLeftClick(cursorPos)
            else {
                var currentMinNode = game.nodes.head
                var minDist = 1000f
                game.nodes.foreach(n => {
                    val dist = n.pos.subtract(cursorPos).length
                    if (dist < minDist) {
                        minDist = dist
                        currentMinNode = n
                    }
                })
                var currentMinLaneNode = currentMinNode.getLaneNodes.head
                minDist = 1000f
                currentMinNode.getLaneNodes.foreach(n => {
                    val dist = n.pos.subtract(cursorPos).length
                    if (dist < minDist) {
                        minDist = dist
                        currentMinLaneNode = n
                    }
                })
                if (currentMinLaneNode.getOutgoingLanes.nonEmpty)
                    game.addCar(new Car(Traveller(currentMinLaneNode.pos, currentMinLaneNode.getOutgoingLanes.head, 0, 1), 100))
            }
        case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) => if (mode != Free) current.onRightClick()
        case _ =>
    }

    def onMovement(): Unit = {
        cursorPos = Vals.terrainRayCollision(Vals.getRay(InputHandler.mousePos), (_, _) => 0, 0.1f)
        allowedPos = cursorPos
        if (mode != Free) {
            NodeSnapper.onMovement(cursorPos)
            current.onMovement(cursorPos)
        }
    }

    def switchNoOfLanes(noOfLanes: Int): Unit = {
        if (this.noOfLanes != noOfLanes && !building) {
            this.noOfLanes = noOfLanes
            if (mode != Free) {
                NodeSnapper.reset(cursorPos)
                current.onMovement(cursorPos)
            }
        }
    }

}
