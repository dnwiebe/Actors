package cse.actors.life

import org.scalatest.path

/**
  * Created by dnwiebe on 1/29/16.
  */
class LifeGridTest extends path.FunSpec {

  describe ("A LifeGrid") {
    val subject = new LifeGrid (5, 4)

    describe ("checkered") {
      val living = Set (Point (1, 1), Point (3, 1), Point (2, 2))
      living.foreach {p => subject.registerLifeAt (p)}

      it ("shows what was set") {
        for (x <- 0 until 5; y <- 0 until 4) {
          val p = Point (x, y)
          val expected = living.contains (p)
          assert (subject.isAliveAt (p) === expected, s"${p} should have been ${expected}")
        }
      }

      describe ("then uncheckered") {
        living.foreach {p => subject.registerDeathAt (p)}

        it ("shows nothing living") {
          for (x <- 0 until 5; y <- 0 until 4) {
            val p = Point (x, y)
            assert (subject.isAliveAt (p) === false, s"${p} should have been false")
          }
        }
      }

      describe ("then cleared") {
        subject.clear ()

        it ("shows nothing living") {
          for (x <- 0 until 5; y <- 0 until 4) {
            val p = Point (x, y)
            assert (subject.isAliveAt (p) === false, s"${p} should have been false")
          }
        }
      }
    }

    describe ("when born on the top edge") {
      val result = expectException {subject.registerLifeAt (Point (3, 0))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot register life on 5x4 grid edge at (3, 0)")
      }
    }
    describe ("when born on the left edge") {
      val result = expectException {subject.registerLifeAt (Point (0, 2))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot register life on 5x4 grid edge at (0, 2)")
      }
    }
    describe ("when born on the bottom edge") {
      val result = expectException {subject.registerLifeAt (Point (2, 4))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot register life on 5x4 grid edge at (2, 4)")
      }
    }
    describe ("when born on the right edge") {
      val result = expectException {subject.registerLifeAt (Point (4, 2))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot register life on 5x4 grid edge at (4, 2)")
      }
    }
    describe ("when a cell is killed on the top edge") {
      val result = expectException {subject.registerDeathAt (Point (3, 0))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot register death on 5x4 grid edge at (3, 0)")
      }
    }
    describe ("when queried past the top edge") {
      val result = expectException {subject.isAliveAt (Point (3, -1))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot query life past 5x4 grid edge at (3, -1)")
      }
    }
    describe ("when queried past the left edge") {
      val result = expectException {subject.isAliveAt (Point (-1, 2))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot query life past 5x4 grid edge at (-1, 2)")
      }
    }
    describe ("when queried past the bottom edge") {
      val result = expectException {subject.isAliveAt (Point (2, 5))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot query life past 5x4 grid edge at (2, 5)")
      }
    }
    describe ("when queried past the right edge") {
      val result = expectException {subject.isAliveAt (Point (5, 2))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot query life past 5x4 grid edge at (5, 2)")
      }
    }
  }

  def expectException (closure: => Unit): Exception = {
    try {
      closure
      fail ("Should have thrown exception")
    }
    catch {
      case e: Exception => e
    }
  }
}
