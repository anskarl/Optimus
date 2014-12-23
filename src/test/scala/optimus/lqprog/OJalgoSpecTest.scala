package optimus.lqprog

import org.scalatest.{FunSpec, Matchers}

/**
 * Specification for oJalgo.
 *
 * @author Vagelis Michelioudakis
 */
final class OJalgoSpecTest extends FunSpec with Matchers {

  describe("Linear programming") {

    describe("Test I") {
      implicit val problem = new LQProblem(SolverLib.OJalgo)

      val x = MPFloatVar("x", 100, 200)
      val y = MPFloatVar("y", 80, 170)

      maximize(-2 * x + 5 * y)
      add(y >= -x + 200)
      start()

      x.value should equal(Some(100))
      y.value should equal(Some(170))
      objectiveValue should equal(650)
      checkConstraints() should be(true)
      status should equal(ProblemStatus.OPTIMAL)

      release()
    }

    describe("Test II") {
      implicit val problem = new LQProblem(SolverLib.OJalgo)

      val x = MPFloatVar("x", 100, 200)
      val y = MPFloatVar("y", 80, 170)

      minimize(-2 * x + 5 * y)
      add(y >= -x + 200)
      start()

      x.value should equal(Some(200))
      y.value should equal(Some(80))
      objectiveValue should equal(0)
      checkConstraints() should be(true)
      status should equal(ProblemStatus.OPTIMAL)
      release()
    }

    describe("Test III") {
      implicit val lp = new LQProblem(SolverLib.OJalgo)

      val x = MPFloatVar("x")
      val y = MPFloatVar("y", 80, 170)

      minimize(-2 * x + 5 * y)
      add(y >= -x + 200)
      start()

      // Solution is infeasible but some solvers consider it dual infeasible
      status should (equal(ProblemStatus.UNBOUNDED) or equal(ProblemStatus.INFEASIBLE))

      release()
    }

    describe("Test IV") {
      implicit val lp = new LQProblem(SolverLib.OJalgo)

      val x = MPFloatVar( "x", 100, 200)
      val y = MPFloatVar("y", 80, 170)

      minimize(-2 * x + 5 * y)

      val z = MPFloatVar(lp, "z", 80, 170)

      add(z >= 170)
      add(y >= -x + 200)
      start()

      x.value should equal(Some(200))
      y.value should equal(Some(80))
      z.value should equal(Some(170))
      objectiveValue should equal(0)
      status should equal(ProblemStatus.OPTIMAL)

      release()
    }

    describe("Test V") {
      implicit val lp = new LQProblem(SolverLib.OJalgo)

      val x = MPFloatVar("x", 100, 200)
      val y = MPFloatVar("y", 80, 170)

      minimize(-2 * x + 5 * y)
      add(y >= -x + 200)
      start()

      x.value should equal(Some(200))
      y.value should equal(Some(80))
      objectiveValue should equal(0)
      status should equal(ProblemStatus.OPTIMAL)

      release()
    }

    describe("Test VI") {
      implicit val lp = new LQProblem(SolverLib.OJalgo)

      val x = MPFloatVar("x", 0, 10)
      val y = MPFloatVar("y", 0, 10)

      maximize(x + y)
      add(x + y >= 5)
      start()

      x.value should equal(Some(10))
      y.value should equal(Some(10))
      objectiveValue should equal(20)
      status should equal(ProblemStatus.OPTIMAL)

      release()
    }

    describe("Test VII") {
      implicit val lp = new LQProblem(SolverLib.OJalgo)

      val x = MPFloatVar("x", 0, 10)
      val y = MPFloatVar("y", 0, 10)

      var cons: Vector[MPConstraint] = Vector()

      maximize(x + y)

      cons = cons :+ add(x + y >= 5)
      cons = cons :+ add(x + 2 * y <= 25)
      cons = cons :+ add(x + 2 * y <= 30)
      cons = cons :+ add(x + y >= 17.5)
      cons = cons :+ add(x := 10.0)

      start()

      x.value.get should be(10.0 +- 1e-6)
      y.value.get should be(7.5 +- 1e-6)

      cons(0).isTight() should be(false)
      cons(1).isTight() should be(true)
      cons(2).isTight() should be(false)
      cons(3).isTight() should be(true)
      cons(4).isTight() should be(true)

      cons(0).slack should be(12.5 +- 1e-6)
      cons(1).slack should be(0.0 +- 1e-6)
      cons(2).slack should be(5.0 +- 1e-6)
      cons(3).slack should be(0.0 +- 1e-6)
      cons(4).slack should be(0.0 +- 1e-6)

      cons.foreach(c => c.check() should be(true))

      objectiveValue should be(17.5 +- 1e-6)
      status should equal(ProblemStatus.OPTIMAL)

      release()
    }
  }

  describe("Quadratic programming") {

    describe("Test I") {
      implicit val lp = new LQProblem(SolverLib.OJalgo)

      val x = MPFloatVar("x", 0, Double.PositiveInfinity)
      val y = MPFloatVar("y", 0, Double.PositiveInfinity)

      minimize(-8*x - 16*y + x*x + 4*y*y)
      add(x + y <= 5)
      add(x <= 3)
      add(x >= 0)
      add(y >= 0)
      start()

      x.value.get should equal (2.9999999998374056 +- 0.0001)
      y.value.get should equal (1.999958833749785 +- 0.0001)
      objectiveValue should be(-3.10000000e+01 +- 0.0001)
      status should equal(ProblemStatus.OPTIMAL)

      release()
    }
  }

  println()
}