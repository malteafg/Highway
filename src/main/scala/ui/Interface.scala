package ui

import graphics.Shader
import input.InputHandler
import math.{Matrix4f, Vector2f, Vector3f, Vector4f}
import org.lwjgl.opengl.GL11._
import utils.{Options, Vals}

import scala.collection.mutable

object Interface {

    val screen: UIComponent = new UIComponent(null, new Vector2f(), new Vector2f(16, 9), new Vector4f(1, 0, 1, 1))
    val panel: UIComponent = new UIComponent(screen, new Vector2f(), new Vector2f(3, 9), Vals.UI_COLOR)

    val button1: Button = new Button(panel, new Vector2f(0.5f, 0.5f), new Vector2f(2, 2), new Vector4f(1, 0, 1, 1),
        () => "Top button", () => Options.log(s"Top button says click!", Options.Button))
    val button2: Button = new Button(panel, new Vector2f(0.5f, 3), new Vector2f(2, 2), new Vector4f(1, 1, 0, 1),
        () => "Middle button", () => Options.log(s"Middle button says click!", Options.Button))
    val button3: Button = new Button(panel, new Vector2f(0.5f, 5.5f), new Vector2f(2, 2), new Vector4f(0, 1, 1, 1),
        () => "Bottom button", () => {
            Options.log(s"Bottom button says click!", Options.Button)
            panel.active = false
        })

    val slider = new Slider(screen, new Vector2f(14, 0), new Vector2f(2, 9), Vals.UI_COLOR, false, false, 1, 0.5f,
        (f: Float) => Options.log(s"Slider has a value of $f", Options.Button))

    val textField = new TextField(screen, new Vector2f(4, 0), new Vector2f(8, 2), Vals.UI_COLOR, "",
        (text: String) => {
            Options.log(s"You entered: '$text'", Options.TextField)
            if(text.toLowerCase() == "open panel") panel.active = true
        })

    def init: Unit = {
        InputHandler.addMousePressSub(mousePressed)
        InputHandler.addMouseScrollSub(mousePressed)
        UIRenderer.init()
    }

    def render(): Unit = {
        glDisable(GL_DEPTH_TEST)

        val elements = new mutable.Queue[UIComponent]()
        elements.enqueue(screen)
        while (!elements.isEmpty) {
            val e = elements.dequeue()
            if (e.isActive()) {
                UIRenderer.drawQuad(e.pos, e.size, e.color)
                elements.enqueueAll(e.getChildren())
            }
        }
        UIRenderer.flush()

        glEnable(GL_DEPTH_TEST)
    }

    def mousePressed(event: (Int, Int, Int)) = {
        (false, screen.click(event))
    }

}
