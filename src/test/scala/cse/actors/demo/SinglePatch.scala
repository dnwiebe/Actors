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
// 4-core 1.4GHz machine: ==== TIME FOR SINGLE PATCH: 283072ms
//
// CLOCK  CORES   CAPABILITY  PERCENT   TIME    1/PERCENT   SPEEDUP
// 3.2GHz 12      38.4        100       142058  100         1.0
// 1.4GHz 4        5.6         15       283072  50          1.0

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
