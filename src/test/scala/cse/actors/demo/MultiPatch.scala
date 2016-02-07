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

// 12-core 3.2GHz machine: ==== TIME FOR 12 PATCHES: 17627ms
// Notes: All cores went to 75% utilization; total memory commitment was not perceptibly affected.
// 4-core 1.4GHz machine: ==== TIME FOR 4 PATCHES: 76766ms
//
// CLOCK  CORES   CAPABILITY  PERCENT   TIME    1/PERCENT SPEEDUP
// 3.2GHz 12      38.4        100       17627   100       8.1
// 1.4GHz 4        5.6         15       76766   23        3.7

class MultiPatch extends FlatSpec {
  PatchHandler.system = ActorSystem ()

  behavior of s"a ${WIDTH}x${HEIGHT} ${GENERATIONS}-generation Conway problem with ${MULTIPATCHES} patches"

  it should "take a certain amount of time" in {
    val coordinator = Coordinator (PatchHandler)
    val grid = new LifeGrid (WIDTH, HEIGHT)
    initializeGrid (grid)
    val startMessage = Start (grid, MULTIPATCHES, GENERATIONS)

    val begin = System.currentTimeMillis ()
    val response: Future[Finished] = (coordinator ? startMessage).mapTo[Finished]
    Await.result (response, 1 hour)
    val end = System.currentTimeMillis ()

    println (s"==== TIME FOR ${MULTIPATCHES} PATCHES: ${end - begin}ms")
  }
}
