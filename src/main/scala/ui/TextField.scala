package ui

import input.InputHandler
import math.{Vector2f, Vector3f}
import utils.Options

class TextField(val p: Vector2f, val s: Vector2f, val c: Vector3f,
                var text: String, val func: String => Unit) extends UIComponent(p, s, c) {

    var typingPos: Int = -1;

    override def click(event: (Int, Int, Int)): Boolean = {
        if(isInside(InputHandler.mousePos) && InputHandler.isPressed(event)) {
            Options.log(s"Click!", Options.TextField)
            InputHandler.addKeyPressSub(write)
            typingPos = text.length
            true
        } else false
    }

    def write(event: (Int, Int, Int)) = {
        var b = false;
        if(InputHandler.isCodePoint(event)) {
            text = text.substring(0, typingPos) + event._1.toChar + text.substring(typingPos, text.length)
            typingPos += 1
            b = true
        } else if(InputHandler.isUnAltered(event) && InputHandler.isPressed(event)) {
            b = true
            event._1 match {
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
                case 261 => {
                    if(typingPos < text.length) {
                        text = text.substring(0, typingPos) + text.substring(typingPos + 1, text.length)
                    }
                }
                case 263 => {
                    if(typingPos > 0) typingPos -= 1
                }
                case 262 => {
                    if(typingPos < text.length) typingPos += 1
                }
                case _ => {
                    b = false
                }
            }


        }
        (typingPos == -1, b)
    }

}
