package graphics

import input.InputHandler
import math.{Matrix4f, Vector2f, Vector3f}
import utils.Vals

class Camera {

    val targetPos = new Vector3f()
    val targetDist = 10.0f
    var pitch = 0f
    var yaw = 0f
    var mousePos: Vector2f = null

    InputHandler.addMousePressSub(click)

    def click(event: (Int, Int, Int)) = {
        if(InputHandler.isPressed(event) && InputHandler.isWheelClick(event)) {
            InputHandler.addMouseMoveSub(drag)
            mousePos = InputHandler.mousePos
            (false, true)
        } else if(InputHandler.isReleased(event) && InputHandler.isWheelClick(event)) {
            mousePos = null
            (false, true)
        } else (false, false)
    }

    def drag(event: (Int, Int, Int)) = {
        if(mousePos != null) {
            val newPos = new Vector2f(event._2, event._3)
            val diff = newPos.subtract(mousePos)

            mousePos = newPos

            yaw = (yaw - diff.x / Vals.WIDTH * 5.0f) % (Math.PI.toFloat * 2)
            pitch = Vals.restrain(pitch + diff.y / Vals.HEIGHT * 3.0f, 0.1f, 1.5f)

            (false, false)
        } else (true, false)
    }

    def getCameraPos = {
        val horizontalDist = Math.cos(pitch).toFloat
        val verticalDist = Math.sin(pitch).toFloat

        val pos = targetPos.add(new Vector3f(Math.sin(yaw).toFloat, 0, -Math.cos(yaw).toFloat).scale(horizontalDist)
                 .add(new Vector3f(0, verticalDist, 0)).scale(targetDist))
        pos
    }

    def getViewMatrix = {
        val m = Matrix4f.rotate(-pitch, 1, 0,0).rotate(yaw, 0, 1, 0).translate(getCameraPos.negate)
        m
    }

}
