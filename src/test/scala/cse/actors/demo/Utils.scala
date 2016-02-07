package cse.actors.demo

import scala.concurrent.duration._
import akka.util.Timeout
import cse.actors.life.{LifeGrid, Point}

import scala.util.Random

/**
  * Created by dnwiebe on 2/4/16.
  */
object Utils {
  val WIDTH = 1000
  val HEIGHT = 1000
  val GENERATIONS = 1000
  val MULTIPATCHES = 4
  val MANYPATCHES = 1000
  val INITIAL_DENSITY = 0.33333
  implicit val TIMEOUT = Timeout (1 hour)

  def initializeGrid (grid: LifeGrid): Unit = {
    val random = new Random ()
    for (y <- 1 until grid.height - 1; x <- 1 until grid.width - 1) {
      if (random.nextDouble < INITIAL_DENSITY) {
        grid.registerLifeAt (Point (x, y))
      }
    }
  }
}
