package input

sealed abstract class Feedback(_unsubscribe: Boolean, _block: Boolean) {

    def unsubscribe: Boolean = _unsubscribe
    def block: Boolean = _block

}

object Feedback {

    case object Passive extends Feedback(false, false)
    case object Unsubscribe extends Feedback(true, false)
    case object Block extends Feedback(false, true)
    case object Both extends Feedback(true, true)

    def custom(unsubscribe: Boolean, block: Boolean): Feedback = (unsubscribe, block) match {
        case (false, false) => Passive
        case (true, false) => Unsubscribe
        case (false, true) => Block
        case (true, true) => Both
    }

}
