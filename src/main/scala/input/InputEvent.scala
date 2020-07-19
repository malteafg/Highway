package input

case class InputEvent(key: Int, action: Int, mods: Int) {

    def isShiftDown()   = mods == 1
    def isControlDown() = mods == 2
    def isAltDown()     = mods == 3

    def isCodePoint()   = mods == -1
    def isUnAltered()   = mods == 0

    def isReleased()    = key != -1 && action == 0
    def isPressed()     = key != -1 && action == 1
    def isContinued()   = action == 2

    def isLeftClick()   = key == 0
    def isRightClick()  = key == 1
    def isWheelClick()  = key == 2

    def isScrolling()   = key == -1

}