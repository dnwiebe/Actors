package cse.actors.life

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.path

/**
  * Created by dnwiebe on 1/30/16.
  */
class PatchHandlerTest (system: ActorSystem) extends TestKit (system) with ImplicitSender with path.FunSpecLike {

  def this () = this (ActorSystem ())
  PatchHandler.system = system

  describe ("A pair of grids") {
    val life = new LifeGrid (9, 9)
    val neighbors = new NeighborGrid (9, 9)

    describe ("with a hyphen inside a square") {
      val squarePoints = (1 to 7).flatMap {i =>
        List (Point (1, i), Point (7, i), Point (i, 1), Point (i, 7))
      }
      squarePoints.foreach {p => life.registerLifeAt (p)}
      (3 to 5).foreach {x => life.registerLifeAt (Point (x, 4))}

      describe ("given to a PatchHandler defined around the hyphen") {
        val subject = PatchHandler (life, neighbors, Point (2, 2), Point (7, 7))

        describe ("which is then turned loose until finished") {
          subject ! Advance ()

          expectMsgClass (classOf[Complete])

          it ("the square is unchanged, but the hyphen is now a pipe") {
            val pipePoints = (3 to 5).map {y => Point (4, y)}
            val livePoints = (squarePoints ++ pipePoints).toSet
            for (x <- 0 until 9; y <- 0 until 9) {
              val p = Point (x, y)
              assert (life.isAliveAt (p) === livePoints.contains (p), s"${p} should have been ${life.isAliveAt (p)}")
            }
          }
        }
      }
    }
  }
}
