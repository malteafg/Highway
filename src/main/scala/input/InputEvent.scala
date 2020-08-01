package input

case class InputEvent(key: Int, action: Int, mods: Int) {

    def isShiftDown: Boolean = mods == 1
    def isControlDown: Boolean = mods == 2
    def isAltDown: Boolean = mods == 3

    def isCodePoint: Boolean = mods == -1
    def isUnAltered: Boolean = mods == 0

    def isReleased: Boolean = key != -1 && action == 0
    def isPressed: Boolean = key != -1 && action == 1
    def isContinued: Boolean = action == 2

    def isLeftClick: Boolean = key == 0
    def isRightClick: Boolean = key == 1
    def isWheelClick: Boolean = key == 2

    def isScrolling: Boolean = key == -1

}