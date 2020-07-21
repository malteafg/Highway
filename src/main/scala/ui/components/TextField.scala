package ui.components

import input.{Feedback, InputEvent, InputHandler}
import utils.math.{Vec2, Vec4}
import utils.Options

class TextField(val par: UIComponent, val p: Vec2, val s: Vec2, val c: Vec4,
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

    def unfocus(event: InputEvent): Feedback = {
        if(event.isPressed() && !isInside(InputHandler.mousePos) ) {
            typingPos = -1
            func(text)
            Feedback.Unsubscribe
        } else Feedback.Passive
    }

    def write(event: InputEvent): Feedback = {
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
            Feedback.custom(typingPos == -1, b)
        } else Feedback.Unsubscribe
    }

}
