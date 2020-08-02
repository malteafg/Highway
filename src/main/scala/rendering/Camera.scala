package rendering

import game.GameHandler
import input.{Feedback, InputEvent, InputHandler}
import utils.math.{Mat4, Vec2, Vec3}
import utils.{Options, Vals}

class Camera {

    // x = pitch, y = yaw, z = dist to target
    var orientation: Vec3 = Vals.CAMERA_STANDARD_ORIENTATION
    var targetPos: Vec3 = Vec3()

    var input: Array[Boolean] = new Array(6)
    var velocity: Array[Int] = new Array(8)

    var dragging: Boolean = false

    var nextOrientation:  Vec3 = Vals.CAMERA_TOPDOWN_ORIENTATION
    var nextTargetPos:    Vec3 = Vec3()

    var progress = 0.0f
    var progressionSpeed: Float = Vals.CAMERA_MOVE_SPEED
    var progressionFunction: Float => Float = smoothMove

    InputHandler.addMousePressSub(click)
    InputHandler.addMouseScrollSub(scroll)
    InputHandler.addKeyPressSub(keyPress)

    def update(): Unit = {
        if(progressionSpeed > 0) {
            val currentNLP = progressionFunction(progress)
            progress       = Math.min(progress + progressionSpeed, 1.0f)
            val nextNLP    = progressionFunction(progress)

            orientation = step(currentNLP, nextNLP, orientation, nextOrientation)
            targetPos   = step(currentNLP, nextNLP, targetPos,   nextTargetPos)

            if(progress == 1.0f) stop()
        } else {
            for(i <- 0 until velocity.length - 2) velocity(i) = Vals.restrain(velocity(i) + (if(input(i)) 1 else -1) , 0, Vals.MAX_CAMERA_SPEED).toInt

            val speed = Vals.CAMERA_MOVE_SPEED * orientation.z
            if(velocity(0) >  0) targetPos = targetPos.add(getDirectionVector(orientation.y + Math.PI.toFloat).scale(speed * velocity(0) / Vals.MAX_CAMERA_SPEED))
            if(velocity(1) >  0) targetPos = targetPos.add(getDirectionVector(orientation.y - Math.PI.toFloat / 2.0f).scale(speed * velocity(1) / Vals.MAX_CAMERA_SPEED))
            if(velocity(2) >  0) targetPos = targetPos.add(getDirectionVector(orientation.y).scale(speed * velocity(2) / Vals.MAX_CAMERA_SPEED))
            if(velocity(3) >  0) targetPos = targetPos.add(getDirectionVector(orientation.y + Math.PI.toFloat / 2.0f).scale(speed * velocity(3) / Vals.MAX_CAMERA_SPEED))
            if(velocity(4) >  0) orientation = orientation.y(Vals.center(orientation.y + velocity(4) * 5f * Vals.CAMERA_MOVE_SPEED / Vals.MAX_CAMERA_SPEED, Math.PI.toFloat))
            if(velocity(5) >  0) orientation = orientation.y(Vals.center(orientation.y - velocity(5) * 5f * Vals.CAMERA_MOVE_SPEED / Vals.MAX_CAMERA_SPEED, Math.PI.toFloat))
            if(velocity(6) != 0 || velocity(7) != 0) {
                velocity(6) += (if(velocity(7) > 0 || (velocity(7) == 0 && velocity(6) < 0)) 1 else -1)
                val dist = newDist(Vals.stepSum(velocity(6)))
                if(Math.abs(velocity(6)) >= Math.sqrt(Math.abs(velocity(7))) * Vals.MAX_CAMERA_SPEED || dist < Vals.MIN_CAMERA_HEIGHT || dist > Vals.MAX_CAMERA_HEIGHT) velocity(7) = 0
                orientation = orientation.z(Vals.restrain(newDist(velocity(6)), Vals.MIN_CAMERA_HEIGHT, Vals.MAX_CAMERA_HEIGHT))
            }
            if(velocity.foldLeft[Boolean](false)((b, i) => b || i != 0)) GameHandler.onMovement()
        }
    }

    def newDist(velocity: Int): Float = orientation.z * Math.pow(1.03f, 1f * velocity / Vals.MAX_CAMERA_SPEED).toFloat

    def move(pos: Vec3, direction: Vec3, speed: Float, func: Float => Float): Unit = {
        nextTargetPos = pos
        nextOrientation = direction
        progressionSpeed = speed
        progressionFunction = func
        progress = 0.0f
    }

    def stop(): Unit = progressionSpeed = 0

    def linearMove(f: Float): Float = f

    def smoothMove(f: Float): Float = (Vals.CAMERA_MOVE_SMOOTH_FACTOR + 1) / 2.0f *
      (2 * f - 1) / Math.sqrt(Vals.square(Vals.CAMERA_MOVE_SMOOTH_FACTOR) * (Vals.square(2 * f - 1) - 1) +
      Vals.square(Vals.CAMERA_MOVE_SMOOTH_FACTOR + 1)).toFloat + 0.5f

    def step(a: Float, b: Float, V: Vec3, T: Vec3): Vec3 = T.scale(b).add(V.subtract(T.scale(a)).scale((1.0f - b)/(1.0f - a)))

    def click(event: InputEvent): Feedback = {
        Options.log("Clicked camera", Options.Camera)
        if(event.isPressed && event.isWheelClick) {
            InputHandler.addMouseMoveSub(drag)
            dragging = true
            stop()
            Feedback.Block
        } else if(event.isReleased && event.isWheelClick) {
            dragging = false
            stop()
            Feedback.Block
        } else Feedback.Passive
    }

    def keyPress(event: InputEvent): Feedback = {
        if(!event.isContinued && event.isUnAltered) {
            var a = !event.isReleased
            var b = true
            event.key match {
                case 87 => input(0) = a
                case 65 => input(1) = a
                case 83 => input(2) = a
                case 68 => input(3) = a
                case 69 => input(4) = a
                case 81 => input(5) = a
                case 32 => if(progressionSpeed <= 0 && a) {
                    move(Vec3(), Vals.CAMERA_STANDARD_ORIENTATION, Vals.CAMERA_MOVE_SPEED, smoothMove)
                    a = false
                }
                case 90 => orientation = orientation.x(Math.PI.toFloat / 2)
                case _ => b = false
            }

            if(b && a) stop()

            Feedback.custom(unsubscribe = false, block = b)
        } else Feedback.Passive
    }

    def scroll(event: InputEvent): Feedback = {
        if((event.mods > 0 && orientation.z > Vals.MIN_CAMERA_HEIGHT) || (event.mods < 0 && orientation.z < Vals.MAX_CAMERA_HEIGHT)) velocity(7) -= event.mods
        stop()
        Feedback.Block
    }

    def drag(event: InputEvent): Feedback = {
        if(dragging) {
            val newPos = Vec2(event.action, event.mods)
            val diff = newPos.subtract(InputHandler.mousePos)

            orientation = orientation.y(Vals.center(orientation.y - diff.x / Vals.WIDTH * 5.0f, Math.PI.toFloat))
            orientation = orientation.x(Vals.restrain(orientation.x + diff.y / Vals.HEIGHT * 3.0f, Vals.MIN_CAMERA_PITCH, Vals.MAX_CAMERA_PITCH))
            stop()

            Feedback.Passive
        } else Feedback.Unsubscribe
    }

    def getCameraPos: Vec3 = {
        val horizontalDist = Math.cos(orientation.x).toFloat
        val verticalDist = Math.sin(orientation.x).toFloat

        val pos = targetPos.add(getDirectionVector(orientation.y).scale(horizontalDist)
          .add(Vec3(0, verticalDist)).scale(orientation.z))
        pos
    }

    def getViewMatrix: Mat4 = Mat4.rotate(-orientation.x, 1, 0,0).rotate(orientation.y, 0, 1, 0).translate(getCameraPos.negate)

    private def getDirectionVector(a: Float) = Vec3(Math.sin(a).toFloat, 0, -Math.cos(a).toFloat)

}
