package ui

import input.InputHandler
import math.{Vector2f, Vector3f}
import utils.{Options, Vars}

object Interface {

    val button1: Button = new Button(new Vector2f(0.5f, 0.5f), new Vector2f(2, 2), new Vector3f(),
        () => "Top button", () => Options.log(s"Top button says click!", Options.Button))
    val button2: Button = new Button(new Vector2f(0.5f, 3), new Vector2f(2, 2), new Vector3f(),
        () => "Middle button", () => Options.log(s"Middle button says click!", Options.Button))
    val button3: Button = new Button(new Vector2f(0.5f, 5.5f), new Vector2f(2, 2), new Vector3f(),
        () => "Bottom button", () => Options.log(s"Bottom button says click!", Options.Button))
    val panel: UIComponent = new UIComponent(Array(button1, button2, button3), new Vector2f(), new Vector2f(3, 9), Vars.UNIT, new Vector3f())
    val slider = new Slider(new Vector2f(14, 0), new Vector2f(2, 9), new Vector3f(), false, false, 1, 0.5f,
        (f: Float) => Options.log(s"Slider has a value of $f", Options.Button))
    val textField = new TextField(new Vector2f(4, 0), new Vector2f(8, 2), new Vector3f(), "",
        (text: String) => Options.log(s"You entered: '$text'", Options.TextField))
    val screen: UIComponent = new UIComponent(Array(panel, slider, textField), new Vector2f(), new Vector2f(16, 9), Vars.UNIT, new Vector3f())

    def init: Unit = {
        InputHandler.addMousePressSub(mousePressed)
        InputHandler.addMouseScrollSub(mousePressed)
    }

    def mousePressed(event: (Int, Int, Int)) = {
        (false, screen.click(event))
    }

}
