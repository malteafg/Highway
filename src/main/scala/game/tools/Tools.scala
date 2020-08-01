package game.tools

import game.Game
import game.terrain.TerrainLine
import input.{InputEvent, InputHandler, Keys, Mouse}
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

    private var allowedPos = Vec3()
    private var cursorPos = Vec3()
    val roadWidth: () => Float = () => noOfLanes * Vals.LARGE_LANE_WIDTH
    private val cursorMarker = new TerrainLine(() => getAllowedPos.xz, () => getAllowedPos.xz.add(Vec2(0.01f)), width = roadWidth, color = Vec4(0.0f, 0.6f, 0.9f, 0.8f))

    def init(game: Game): Unit = {
        this.game = game
        NodeSnapper.init(game.nodes)
    }

    /**
     * Functions
     */
    def straightRoad(): Unit = {
        if (stack.isEmpty) stack.push(SelectPos())
        if (mode != Straight) current.onModeSwitch(Straight)
        mode = Straight
    }

    def curvedRoad(): Unit = {
        if (stack.isEmpty) stack.push(SelectPos())
        if (mode != Curved) current.onModeSwitch(Curved)
        mode = Curved
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
    def back(): Unit = stack.pop()

    def current: State = try stack.top catch {
        case _: IndexOutOfBoundsException | _: NoSuchElementException => null
    }

    def push(state: State): Unit = {
        stack.push(state)
        Options.log(s"State pushed on tool stack: $state", Options.State)
    }

    def remove(): Unit = stack.pop()

    def replace(state: State): Unit = {
        remove()
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

    /**
     * Input
     */
    def onKeyPress(e: InputEvent): Unit = e match {
        case InputEvent(49, Keys.PRESSED, _) => noOfLanes = 1
        case InputEvent(50, Keys.PRESSED, _) => noOfLanes = 2
        case InputEvent(51, Keys.PRESSED, _) => noOfLanes = 3
        case InputEvent(52, Keys.PRESSED, _) => noOfLanes = 4
        case InputEvent(170, Keys.PRESSED, _) => straightRoad()
        case InputEvent(143, Keys.PRESSED, _) => curvedRoad()
        case _ =>
    }

    def onMousePress(e: InputEvent): Unit = e match {
        case InputEvent(Mouse.LEFT, Mouse.PRESSED, _) => if (mode != Free) current.onLeftClick(cursorPos)
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

}
