package cse.actors.demo

import akka.actor.ActorSystem
import akka.pattern.ask
import cse.actors.demo.Utils._
import cse.actors.life.PatchHandler._
import cse.actors.life._
import org.scalatest.FlatSpec

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by dnwiebe on 2/4/16.
  */

// 12-core 3.2GHz machine: ==== TIME FOR 1000 PATCHES: 32719ms
// Notes: All cores went to 100% utilization; total memory commitment increased by perhaps 50%.
// 4-core 1.4GHz machine: ==== TIME FOR 1000 PATCHES: 93973ms
//
// CLOCK  CORES   CAPABILITY  PERCENT   TIME    1/PERCENT SPEEDUP
// 3.2GHz 12      38.4        100       32719   100       4.3
// 1.4GHz 4        5.6         15       93973   35        3.0

class ManyPatch extends FlatSpec {
  PatchHandler.system = ActorSystem ()

  behavior of s"a ${WIDTH}x${HEIGHT} ${GENERATIONS}-generation Conway problem with ${MANYPATCHES} patches"

  it should "take a certain amount of time" in {
    val coordinator = Coordinator (PatchHandler)
    val grid = new LifeGrid (WIDTH, HEIGHT)
    initializeGrid (grid)
    val startMessage = Start (grid, MANYPATCHES, GENERATIONS)

    val begin = System.currentTimeMillis ()
    val response: Future[Finished] = (coordinator ? startMessage).mapTo[Finished]
    Await.result (response, 1 hour)
    val end = System.currentTimeMillis ()

    println (s"==== TIME FOR ${MANYPATCHES} PATCHES: ${end - begin}ms")
  }
}
