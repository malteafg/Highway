package ui

import math.{Vector2f, Vector3f}
import utils.{Options, Vars}

object Interface {


    val button1: Button = new Button(new Vector2f(Vars.UNIT * 0.5f, Vars.UNIT * 0.5f), new Vector2f(Vars.UNIT * 2, Vars.UNIT * 2), new Vector3f(),
        () => "Top button", () => Options.log(s"Top button says click!", Options.Button))
    val button2: Button = new Button(new Vector2f(Vars.UNIT * 0.5f, Vars.UNIT * 3), new Vector2f(Vars.UNIT * 2, Vars.UNIT * 2), new Vector3f(),
        () => "Middle button", () => Options.log(s"Middle button says click!", Options.Button))
    val button3: Button = new Button(new Vector2f(Vars.UNIT * 0.5f, Vars.UNIT * 5.5f), new Vector2f(Vars.UNIT * 2, Vars.UNIT * 2), new Vector3f(),
        () => "Bottom button", () => Options.log(s"Bottom button says click!", Options.Button))
    val panel: UIComponent = new UIComponent(Array(button1, button2, button3), new Vector2f(), new Vector2f(Vars.UNIT * 3, Vars.UNIT * 9), new Vector3f())

    def mousePressed(vec: Vector2f, event: (Int, Int, Int)) = {
        panel.click(vec, event)
    }

}
