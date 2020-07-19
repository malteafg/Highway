package ui.components

import input.InputHandler
import input.InputEvent
import utils.math.{Vector2f, Vector4f}
import utils.Options

class TextField(val par: UIComponent, val p: Vector2f, val s: Vector2f, val c: Vector4f,
                var text: String, val func: String => Unit) extends UIComponent(par, p, s, c) {

    var typingPos: Int = -1

    /*
     * Functions
     */
    override def click(event: InputEvent): Boolean = {
        if(event.isPressed() && isInside(InputHandler.mousePos) ) {
            Options.log(s"Writing!", Options.TextField)
            if(typingPos == -1) {
                InputHandler.addCharSub(write)
                InputHandler.addMousePressSub(unfocus)
            }

            typingPos = text.length
            true
        } else false
    }

    def unfocus(event: InputEvent) = {
        if(event.isPressed() && !isInside(InputHandler.mousePos) ) {
            typingPos = -1
            func(text)
            (true, false)
        } else (false, false)
    }

    def write(event: InputEvent) = {
        if(typingPos > -1) {
            var b = false
            if(event.isCodePoint()) {
                text = text.substring(0, typingPos) + event.key.toChar + text.substring(typingPos, text.length)
                typingPos += 1
                b = true
            } else if(event.isUnAltered() && (event.isPressed() || event.isContinued())) {
                b = true
                event.key match {
                    case 257 => {
                        typingPos = -1
                        func(text)
                    }
                    case 259 => {
                        if(typingPos > 0) {
                            text = text.substring(0, typingPos - 1) + text.substring(typingPos, text.length)
                            typingPos -= 1
                        }
                    }
                    case 261 => if(typingPos < text.length) text = text.substring(0, typingPos) + text.substring(typingPos + 1, text.length)
                    case 263 => if(typingPos > 0) typingPos -= 1
                    case 262 =>  if(typingPos < text.length) typingPos += 1
                    case _ => b = false
                }
            }
            (typingPos == -1, b)
        } else (true, false)
    }

}
