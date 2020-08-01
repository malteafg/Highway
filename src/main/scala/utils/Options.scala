package utils

object Options {

    abstract class Log {
        var logging: Boolean = false
        var parent: Log = _
        var name: String = "Log"
        def log(): Boolean = if(logging || parent == null) logging else {
            logging = parent.log()
            logging
        }
    }

    case object Log             extends Log
    case object Game            extends Log { parent = Log;         name = "Game" }
    case object Roads           extends Log { parent = Game;        name = "Roads" }
    case object Camera          extends Log { parent = Log;         name = "Camera" }
    case object Input           extends Log { parent = Log;         name = "Input" }
    case object FPS             extends Log { parent = Log;         name = "FPS" }
    case object State           extends Log { parent = Log;         name = "State" }
    case object Mouse           extends Log { parent = Input;       name = "Mouse" }
    case object MouseMoved      extends Log { parent = Mouse;       name = "MouseMoved" }
    case object MousePressed    extends Log { parent = Mouse;       name = "MousePressed" }
    case object MouseScrolled   extends Log { parent = Mouse;       name = "MouseScrolled" }
    case object Keys            extends Log { parent = Input;       name = "Keys" }
    case object Characters      extends Log { parent = Input;       name = "Characters" }
    case object Interface       extends Log { parent = Log;         name = "Interface" }
    case object Button          extends Log { parent = Interface;   name = "Button" }
    case object TextField       extends Log { parent = Interface;   name = "TextField" }

    // Remember to add new objects to list
    var commands = List(Log, Game, Roads, Camera, Input, FPS, State, Mouse, MouseMoved, MousePressed, MouseScrolled, Keys, Characters, Interface, Button, TextField)


    def setLogging(args: Array[String]): Unit = {
        for (a <- args; c <- commands) if (a == c.name) c.logging = true
        for (c <- commands) c.log()
    }

    def log(message: String, t: Log): Unit = if(t.logging) println(message)

}
