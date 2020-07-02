package utils

object Options {

    abstract class Log {
        var logging: Boolean = false
        var parent: Log = null
        var name: String = "Log"
        def log(): Boolean = if(logging) true else if(parent == null) logging else {
            logging = parent.log()
            logging
        }
    }

    case object Log extends Log
    case object Input extends Log { parent = Log; name = "Input" }
    case object Mouse extends Log { parent = Input; name = "Mouse" }
    case object Keys extends Log { parent = Input; name = "Keys" }

    var commands = List(Log, Input, Mouse, Keys)

    def setLogging(args: Array[String]): Unit = {
        for (a <- args; c <- commands) if (a == c.name) c.logging = true
        for (c <- commands) c.log()
    }

    def log(message: String, t: Log): Unit = if(t.logging) println(message)

}
