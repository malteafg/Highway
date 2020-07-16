package ui

import rendering.{UIRenderer}
import input.InputHandler
import utils.math.{Vector2f, Vector4f}
import ui.components.{Button, Slider, TextField, UIComponent}
import utils.graphics.{Texture}
import utils.{Options, Vals}

object Interface {

    val screen: UIComponent = new UIComponent(null, new Vector2f(), new Vector2f(16, 9), new Vector4f(1, 0, 1, 0))
    val panel: UIComponent = new UIComponent(screen, new Vector2f(), new Vector2f(3, 9), Vals.UI_COLOR)

    val button1: Button = new Button(panel, new Vector2f(0.5f, 0.5f), new Vector2f(2, 2), new Vector4f(1, 0, 1, 1),
        () => "Top button", () => Options.log(s"Top button says click!", Options.Button))

    button1.addTexture(new Texture("logo"))

    val button2: Button = new Button(panel, new Vector2f(0.5f, 3), new Vector2f(2, 2), new Vector4f(1, 1, 0, 1),
        () => "Middle button", () => Options.log(s"Middle button says click!", Options.Button))
    val button3: Button = new Button(panel, new Vector2f(0.5f, 5.5f), new Vector2f(2, 2), new Vector4f(0, 1, 1, 1),
        () => "Bottom button", () => {
            Options.log(s"Bottom button says click!", Options.Button)
            panel.deactivate
        })

    val slider = new Slider(screen, new Vector2f(14, 0), new Vector2f(2, 9), Vals.UI_COLOR, false, false, 1, 0.5f,
        (f: Float) => Options.log(s"Slider has a value of $f", Options.Button))

    val textField = new TextField(screen, new Vector2f(4, 0), new Vector2f(8, 2), Vals.UI_COLOR, "",
        (text: String) => {
            Options.log(s"You entered: '$text'", Options.TextField)
            if(text.toLowerCase() == "open panel") panel.activate
        })

    def init: Unit = {
        InputHandler.addMousePressSub(mousePressed)
        InputHandler.addMouseScrollSub(mousePressed)
        UIRenderer.init()
    }

    def render(): Unit = {
        UIRenderer.render(screen)
    }

    def mousePressed(event: (Int, Int, Int)) = {
        (false, screen.click(event))
    }

}
