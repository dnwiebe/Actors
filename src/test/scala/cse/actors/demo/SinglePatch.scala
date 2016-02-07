package cse.actors.demo

import akka.actor.ActorSystem
import akka.pattern.ask
import cse.actors.life.PatchHandler._
import cse.actors.life._
import cse.actors.demo.Utils._
import org.scalatest.FlatSpec

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by dnwiebe on 2/4/16.
  */

// 12-core 3.2GHz machine: ==== TIME FOR SINGLE PATCH: 142058ms
// Notes: Individual cores went to 85% utilization; total memory commitment was not perceptibly affected.

class SinglePatch extends FlatSpec {
  PatchHandler.system = ActorSystem ()

  behavior of s"a single-patch ${WIDTH}x${HEIGHT} ${GENERATIONS}-generation Conway problem"

  it should "take a certain amount of time" in {
    val coordinator = Coordinator (PatchHandler)
    val grid = new LifeGrid (WIDTH, HEIGHT)
    initializeGrid (grid)
    val startMessage = Start (grid, 1, GENERATIONS)

    val begin = System.currentTimeMillis ()
    val response: Future[Finished] = (coordinator ? startMessage).mapTo[Finished]
    Await.result (response, 1 hour)
    val end = System.currentTimeMillis ()

    println (s"==== TIME FOR SINGLE PATCH: ${end - begin}ms")
  }
}
