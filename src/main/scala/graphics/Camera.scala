package graphics

import input.InputHandler
import math.{Matrix4f, Vector2f, Vector3f}
import utils.{Options, Vals}

class Camera {

    var targetPos = new Vector3f()
    var targetDist = 10.0f
    var pitch = 0f
    var yaw = 0f
    var mousePos: Vector2f = null

    InputHandler.addMousePressSub(click)
    InputHandler.addMouseScrollSub(scroll)
    InputHandler.addKeyPressSub(keyPress)

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

    def keyPress(event: (Int, Int, Int)) = {
        if((InputHandler.isPressed(event) || InputHandler.isContinued(event)) && InputHandler.isUnAltered(event)) {
            event._1 match {
                case 87 => targetPos = targetPos.add(getDirectionVector(yaw + Math.PI.toFloat).scale(0.1f))
                case 65 => targetPos = targetPos.add(getDirectionVector(yaw - Math.PI.toFloat / 2.0f).scale(0.1f))
                case 83 => targetPos = targetPos.add(getDirectionVector(yaw).scale(0.1f))
                case 68 => targetPos = targetPos.add(getDirectionVector(yaw + Math.PI.toFloat / 2.0f).scale(0.1f))
                case 69 => yaw -= 0.2f
                case 81 => yaw += 0.2f
                case _ => 0.0f
            }

            (false, true)
        } else (false, false)
    }

    def scroll(event: (Int, Int, Int)) = {

        targetDist = Vals.restrain(targetDist * Math.pow(1.1f, -event._3).toFloat, 3, 100)

        (false, true)
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

        val pos = targetPos.add(getDirectionVector(yaw).scale(horizontalDist)
                 .add(new Vector3f(0, verticalDist, 0)).scale(targetDist))
        pos
    }

    def getViewMatrix = {
        val m = Matrix4f.rotate(-pitch, 1, 0,0).rotate(yaw, 0, 1, 0).translate(getCameraPos.negate)
        m
    }

    private def getDirectionVector(a: Float) = {
        new Vector3f(Math.sin(a).toFloat, 0, -Math.cos(a).toFloat)
    }

}
