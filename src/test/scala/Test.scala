import org.scalatest.funsuite.AnyFunSuite
import utils.Bezier
import utils.math.Vec3


class Test extends AnyFunSuite {

    test("Rays should intersect") {
        val v = Vec3().intersection(Vec3(1, 0, 1), Vec3(2), Vec3(-1, 0, 1))
        assert(v.x == 1 && v.z == 1)
    }

    test("Rays should not intersect") {
        val v = Vec3().intersection(Vec3(0, 0, 1), Vec3(2), Vec3(-1, 0, -1))
        assert(v.x == 0 && v.z == -2)
    }

    test("Rays should be parallel") {
        val v = Vec3().intersection(Vec3(0, 0, 1), Vec3(2), Vec3(0, 0, -1))
        assert(v == null)
    }

    test("Double snap should not be accepted") {
        val v = Bezier.doubleSnapCurve(Vec3(), Vec3(0.01f, 0, 1), Vec3(2), Vec3(-1, 0, 0.01f), 2)
        assert(v == null)
    }

}