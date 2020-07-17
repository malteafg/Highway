package rendering

import input.InputHandler
import utils.math.{Matrix4f, Vector2f, Vector3f}
import utils.{Options, Vals}

class Camera {

    // x = pitch, y = yaw, z = dist to target
    var orientation = Vals.CAMERA_STANDARD_ORIENTATION
    var targetPos   = new Vector3f()

    var input: Array[Boolean] = new Array[Boolean](6)
    
    var dragging: Boolean = false

    var nextOrientation:  Vector3f = new Vector3f(0.4f, 0, 60.0f)
    var nextTargetPos:    Vector3f = new Vector3f(0, 0f, -10.0f)

    var progress = 0.0f
    var progressionSpeed = Vals.CAMERA_MOVE_SPEED
    var progressionFunction: Float => Float = smoothMove

    InputHandler.addMousePressSub(click)
    InputHandler.addMouseScrollSub(scroll)
    InputHandler.addKeyPressSub(keyPress)

    def update = {

        if(progressionSpeed > 0) {
            val currentNLP = progressionFunction(progress)
            progress       = Math.min(progress + progressionSpeed, 1.0f)
            val nextNLP    = progressionFunction(progress)
            
            orientation = step(currentNLP, nextNLP, orientation, nextOrientation)
            targetPos   = step(currentNLP, nextNLP, targetPos,   nextTargetPos)
            
            if(progress == 1.0f) stop
        } else {
            val speed = Vals.CAMERA_MOVE_SPEED * orientation.z
            if(input(0)) targetPos = targetPos.add(getDirectionVector(orientation.y + Math.PI.toFloat).scale(speed))
            if(input(1)) targetPos = targetPos.add(getDirectionVector(orientation.y - Math.PI.toFloat / 2.0f).scale(speed))
            if(input(2)) targetPos = targetPos.add(getDirectionVector(orientation.y).scale(speed))
            if(input(3)) targetPos = targetPos.add(getDirectionVector(orientation.y + Math.PI.toFloat / 2.0f).scale(speed))
            if(input(4)) orientation.y = Vals.center(orientation.y + 5 * Vals.CAMERA_MOVE_SPEED, Math.PI.toFloat)
            if(input(5)) orientation.y = Vals.center(orientation.y - 5 * Vals.CAMERA_MOVE_SPEED, Math.PI.toFloat)
        }
    }
    
    def move(pos: Vector3f, direction: Vector3f, speed: Float, func: Float => Float): Unit = {
        nextTargetPos = pos
        nextOrientation = direction
        progressionSpeed = speed
        progressionFunction = func
        progress = 0.0f
    }

    def stop = progressionSpeed = 0
    
    def linearMove(f: Float) = f
    
    def smoothMove(f: Float) = (Vals.CAMERA_MOVE_SMOOTH_FACTOR + 1) / 2.0f *
        (2 * f - 1) / Math.sqrt(Vals.square(Vals.CAMERA_MOVE_SMOOTH_FACTOR) * (Vals.square(2 * f - 1) - 1) +
        Vals.square(Vals.CAMERA_MOVE_SMOOTH_FACTOR + 1)).toFloat + 0.5f
    
    def step(a: Float, b: Float, V: Vector3f, T: Vector3f) = T.scale(b).add(V.subtract(T.scale(a)).scale((1.0f - b)/(1.0f - a)))
    
    def click(event: (Int, Int, Int)) = {
        if(InputHandler.isPressed(event) && InputHandler.isWheelClick(event)) {
            InputHandler.addMouseMoveSub(drag)
            dragging = true
            stop
            (false, true)
        } else if(InputHandler.isReleased(event) && InputHandler.isWheelClick(event)) {
            dragging = false
            stop
            (false, true)
        } else (false, false)
    }

    def keyPress(event: (Int, Int, Int)) = {
        if(!InputHandler.isContinued(event) && InputHandler.isUnAltered(event)) {
            var a = !InputHandler.isReleased(event)
            var b = true
            
            event._1 match {
                case 87 => input(0) = a
                case 65 => input(1) = a
                case 83 => input(2) = a
                case 68 => input(3) = a
                case 69 => input(4) = a
                case 81 => input(5) = a
                case 32 => if(progressionSpeed <= 0 && a) {
                        move(new Vector3f(), Vals.CAMERA_STANDARD_ORIENTATION, Vals.CAMERA_MOVE_SPEED, smoothMove)
                        a = false
                }
                case _ => b = false
            }
            
            if(b && a) stop
            
            (false, b)
        } else (false, false)
    }

    def scroll(event: (Int, Int, Int)) = {
        orientation.z = Vals.restrain(orientation.z * Math.pow(1.1f, -event._3).toFloat, 3, 100)
        stop
        (false, true)
    }

    def drag(event: (Int, Int, Int)) = {
        if(dragging) {
            val newPos = new Vector2f(event._2, event._3)
            val diff = newPos.subtract(InputHandler.mousePos)

            orientation.y = Vals.center(orientation.y - diff.x / Vals.WIDTH * 5.0f, Math.PI.toFloat)
            orientation.x = Vals.restrain(orientation.x + diff.y / Vals.HEIGHT * 3.0f, Vals.MIN_CAMERA_PITCH, Vals.MAX_CAMERA_PITCH)
            stop
            
            (false, false)
        } else (true, false)
    }
    
    def getCameraPos = {
        val horizontalDist = Math.cos(orientation.x).toFloat
        val verticalDist = Math.sin(orientation.x).toFloat

        val pos = targetPos.add(getDirectionVector(orientation.y).scale(horizontalDist)
                 .add(new Vector3f(0, verticalDist, 0)).scale(orientation.z))
        pos
    }

    def getViewMatrix = Matrix4f.rotate(-orientation.x, 1, 0,0).rotate(orientation.y, 0, 1, 0).translate(getCameraPos.negate)

    private def getDirectionVector(a: Float) = new Vector3f(Math.sin(a).toFloat, 0, -Math.cos(a).toFloat)

}
