package cse.actors.life

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.path
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.ArgumentCaptor

import scala.collection.mutable.ListBuffer

/**
  * Created by dnwiebe on 1/31/16.
  */
class CoordinatorTest (system: ActorSystem) extends TestKit (system) with ImplicitSender with path.FunSpecLike {

  def this () = this (ActorSystem ())
  implicit val isystem = system

  describe ("A Coordinator with a mocked factory") {
    val factory = mock (classOf[PatchHandlerFactory])
    val subject = Coordinator (factory)

    describe ("given a 120x180 LifeGrid and directed to run 2 generations with 12 patches") {
      implicit val isystem = system
      val dsl = when (factory.apply(any[LifeGrid] (), any[NeighborGrid] (), any[Point] (), any[Point] ()))
      val handlers = (0 until 12).map {idx => TestActorRef (mockHandlerFactory, s"Mock${idx}")}.toList
      handlers.foldLeft (dsl) {(soFar, elem) => soFar.thenReturn (elem)}
      val grid = new LifeGrid (120, 180)

      subject ! Start(grid, 12, 2)
      expectMsg (Finished ())

      it ("behaves as expected") {
        val lifeCaptor = ArgumentCaptor.forClass (classOf [LifeGrid])
        val neighborsCaptor = ArgumentCaptor.forClass (classOf [NeighborGrid])
        val upLeftCaptor = ArgumentCaptor.forClass (classOf [Point])
        val loRightCaptor = ArgumentCaptor.forClass (classOf [Point])
        verify (factory, times (12)).apply (lifeCaptor.capture (), neighborsCaptor.capture (),
          upLeftCaptor.capture (), loRightCaptor.capture ())
        val constructorCalls = (0 until 12).map { idx =>
          assert (lifeCaptor.getAllValues.get (idx) eq grid)
          (neighborsCaptor.getAllValues.get (idx), upLeftCaptor.getAllValues.get (idx),
            loRightCaptor.getAllValues.get (idx))
        }
        verifyNoMoreInteractions (factory)
        val neighborsGrids = constructorCalls.map {_._1}
        assert (neighborsGrids.tail.forall {_ eq neighborsGrids.head})
        val corners = constructorCalls.map { q => (q._2, q._3) }
        assert (corners === List (
          (Point (0,   0), Point (41,  46)), (Point (40,   0), Point (81,  46)), (Point (80,   0), Point (120,  46)),
          (Point (0,  45), Point (41,  91)), (Point (40,  45), Point (81,  91)), (Point (80,  45), Point (120,  91)),
          (Point (0,  90), Point (41, 136)), (Point (40,  90), Point (81, 136)), (Point (80,  90), Point (120, 136)),
          (Point (0, 135), Point (41, 180)), (Point (40, 135), Point (81, 180)), (Point (80, 135), Point (120, 180))
        ))
        handlers.foreach { href =>
          val handler = href.underlyingActor
          assert (handler.log === List (Advance (), Advance ()))
        }
      }
    }
  }

  describe ("chooseDimensions for patches on grid") {
    it ("works for 7") {
      assert (Coordinator.chooseDimensions (7) === (1, 7))
    }

    it ("works for 12") {
      assert (Coordinator.chooseDimensions (12) === (3, 4))
    }

    it ("works for 16") {
      assert (Coordinator.chooseDimensions (16) === (4, 4))
    }

    it ("works for 20") {
      assert (Coordinator.chooseDimensions (20) === (4, 5))
    }

    it ("works for 21") {
      assert (Coordinator.chooseDimensions (21) === (3, 7))
    }
  }

  describe ("chooseRectangles for individual patches") {
    it ("works for 3, 4, 1000, 1000") {
      assert (Coordinator.chooseRectangles (3, 4, 1000, 1000) === List (
        (Point (0,   0), Point (334,  251)), (Point (333,   0), Point (667,  251)), (Point (666,   0), Point (1000,  251)),
        (Point (0, 250), Point (334,  501)), (Point (333, 250), Point (667,  501)), (Point (666, 250), Point (1000,  501)),
        (Point (0, 500), Point (334,  751)), (Point (333, 500), Point (667,  751)), (Point (666, 500), Point (1000,  751)),
        (Point (0, 750), Point (334, 1000)), (Point (333, 750), Point (667, 1000)), (Point (666, 750), Point (1000, 1000))
      ))
    }
  }

  class MockPatchHandler extends PatchHandler (null, null, null, null) {

    val log = ListBuffer[Any] ()

    override def receive = {
      case msg: Advance => log.append (msg); sender ! Complete ()
      case msg => log.append (msg)
    }
  }

  private def mockHandlerFactory = new MockPatchHandler ()
}
