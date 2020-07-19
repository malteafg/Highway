package input

case class InputEvent(key: Int, action: Int, mods: Int) {

    def isControlDown() = mods == 2
    def isShiftDown()   = mods == 1
    def isAltDown()     = mods == 3

    def isCodePoint()   = mods == -1
    def isUnAltered()   = mods == 0

    def isContinued()   = action == 2
    def isPressed()     = key != -1 && action == 1
    def isReleased()    = key != -1 && action == 0

    def isLeftClick()   = key == 1
    def isWheelClick()  = key == 2
    def isRightClick()  = key == 3
    def isScrolling()   = key == -1

}