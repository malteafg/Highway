package utils

object Options {

    abstract class Log {
        var logging: Boolean = false
        var parent: Log = null
        def log(): Boolean = if(logging) true else if(parent == null) logging else {
            logging = parent.log();
            logging
        }
    }
    case object Log extends Log
    case object Input extends Log { parent = Log }
    case object Mouse extends Log { parent = Input }
    case object Keys extends Log { parent = Input }

    def setLogging(args: Array[String]): Unit = {
        for (x <- args) x match {
            case "Input" => Input.logging = true
            case "Mouse" => Mouse.logging = true
            case "Keys" => Keys.logging = true
        }
        Input.log()
        Mouse.log()
        Keys.log()
    }

    def log(message: String, t: Log): Unit = if(t.logging) println(message)

}
