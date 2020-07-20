package ui

import game.GameHandler
import rendering.{GameRenderer, UIRenderer}
import input.{Feedback, InputEvent, InputHandler}
import utils.math.{Vector2f, Vector4f}
import ui.components.{Button, RadioButton, UIComponent}
import utils.graphics.Texture
import utils.{Options, Vals}

object Interface {

    val screen: UIComponent = new UIComponent(null, new Vector2f(), new Vector2f(16, 9), new Vector4f(1, 0, 1, 0))
    val panel: UIComponent = new UIComponent(screen, new Vector2f(1, 8), new Vector2f(14, 1), Vals.UI_COLOR)

    val button1: RadioButton = new RadioButton(panel, new Vector2f(0.1f, 0.1f), new Vector2f(0.8f, 0.8f), new Vector4f(1, 0, 1, 1),
        () => "Top button", (b: Boolean) => {
            Options.log(s"Top button says click!", Options.Button)
            GameRenderer.darkEdges = b
        }, false, 0)

    button1.addTexture(new Texture("logo"))

    val button2: Button = new Button(panel, new Vector2f(1.1f, 0.1f), new Vector2f(0.8f, 0.8f), new Vector4f(1, 1, 0, 1),
        () => "Middle button", () => {
            Options.log(s"Middle button says click!", Options.Button)
            GameHandler.game.spheres.clear()
        })

    val straight: Button = new Button(panel, new Vector2f(2.1f, 0.1f), new Vector2f(0.8f, 0.8f), new Vector4f(0, 1, 1, 1),
        () => "Bottom button", () => {
            Options.log(s"Bottom button says click!", Options.Button)
            GameHandler.straightRoad()
        })

    val curved: Button = new Button(panel, new Vector2f(3.1f, 0.1f), new Vector2f(0.8f, 0.8f), new Vector4f(0, 1, 0, 1),
        () => "Bottom button", () => {
            Options.log(s"Bottom button says click!", Options.Button)
            GameHandler.curvedRoad()
        })

    /*
    val slider = new Slider(screen, new Vector2f(15.5f, 0), new Vector2f(0.5f, 9), Vals.UI_COLOR, false, false, 0.5f, 0.5f,
        (f: Float) => {
            Options.log(s"Slider has a value of $f", Options.Button)
            GameHandler.camera.orientation.x = 0.5f - f / 2 + Vals.MIN_CAMERA_PITCH
        })
    val yawSlider = new Slider(screen, new Vector2f(15.0f, 0), new Vector2f(0.5f, 9), Vals.UI_COLOR, false, false, 0.5f, 0.5f,
        (f: Float) => {
            Options.log(s"Slider has a value of $f", Options.Button)
            GameHandler.camera.orientation.y = f - 0.5f
        })

    val textField = new TextField(screen, new Vector2f(4, 0), new Vector2f(8, 1), Vals.UI_COLOR, "",
        (text: String) => {
            Options.log(s"You entered: '$text'", Options.TextField)
            if(text.toLowerCase() == "open panel") panel.activate
            if(text.toLowerCase() == "close all") screen.deactivate()
        })
     */

    def init(): Unit = {
        InputHandler.addMousePressSub(mousePressed)
        InputHandler.addMouseScrollSub(mousePressed)
        UIRenderer.init()
    }

    def render(): Unit = UIRenderer.render(screen)

    def mousePressed(event: InputEvent): Feedback = Feedback.custom(false, block = screen.click(event))

}
