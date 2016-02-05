package cse.actors.life

import org.scalatest.path

/**
  * Created by dnwiebe on 1/29/16.
  */
class NeighborGridTest extends path.FunSpec {

  describe ("A NeighborGrid") {
    val subject = new NeighborGrid (5, 4)

    describe ("with some neighbors added") {
      val neighbors = Map (Point (1, 1) -> 5, Point (3, 1) -> 1, Point (2, 2) -> 8)
      neighbors.foreach {pair =>
        val (point, count) = pair
        (1 to count).foreach {_ => subject.addNeighbor (point)}
      }

      it ("shows what was set") {
        for (x <- 0 until 5; y <- 0 until 4) {
          val p = Point (x, y)
          val expected = neighbors.getOrElse (p, 0)
          assert (subject.neighborCount (p) === expected, s"${p} should have been ${expected}")
        }
      }

      describe ("then cleared") {
        subject.clear ()

        it ("shows no neighbors") {
          for (x <- 0 until 5; y <- 0 until 4) {
            val p = Point (x, y)
            assert (subject.neighborCount (p) === 0, s"${p} should have been 0")
          }
        }
      }
    }

    describe ("when added past the top edge") {
      val result = expectException {subject.addNeighbor (Point (3, -1))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot add neighbor past 5x4 grid edge at (3, -1)")
      }
    }
    describe ("when added past the left edge") {
      val result = expectException {subject.addNeighbor (Point (-1, 2))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot add neighbor past 5x4 grid edge at (-1, 2)")
      }
    }
    describe ("when added past the bottom edge") {
      val result = expectException {subject.addNeighbor (Point (2, 5))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot add neighbor past 5x4 grid edge at (2, 5)")
      }
    }
    describe ("when added past the right edge") {
      val result = expectException {subject.addNeighbor (Point (5, 2))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot add neighbor past 5x4 grid edge at (5, 2)")
      }
    }

    describe ("when queried past the top edge") {
      val result = expectException {subject.neighborCount (Point (3, -1))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot get count past 5x4 grid edge at (3, -1)")
      }
    }
    describe ("when queried past the left edge") {
      val result = expectException {subject.neighborCount (Point (-1, 2))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot get count past 5x4 grid edge at (-1, 2)")
      }
    }
    describe ("when queried past the bottom edge") {
      val result = expectException {subject.neighborCount (Point (2, 5))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot get count past 5x4 grid edge at (2, 5)")
      }
    }
    describe ("when queried past the right edge") {
      val result = expectException {subject.neighborCount (Point (5, 2))}

      it ("complains appropriately") {
        assert (result.isInstanceOf[IllegalArgumentException])
        assert (result.getMessage === "Cannot get count past 5x4 grid edge at (5, 2)")
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
