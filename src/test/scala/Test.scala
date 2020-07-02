import org.scalatest.funsuite.AnyFunSuite


class Test extends AnyFunSuite {

    test("1 times 0 should be 0") {
        assert(Syntax.func(0, 1.0f) == 0)
    }

    test("10 times 20 should be 200") {
        assert(Syntax.func(10, 20.0f) == 200)
    }

    test("1 times 20 should be 20") {
        assert(Syntax.func(1, 20.0f) == 20)
    }
}