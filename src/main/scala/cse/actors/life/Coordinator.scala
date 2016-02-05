package cse.actors.life

import akka.actor.{Props, ActorRef, ActorSystem, Actor}

/**
  * Created by dnwiebe on 1/31/16.
  */

case class Start (life: LifeGrid, patches: Int, generations: Int)
case class Finished ()

object Coordinator {

  def apply (factory: PatchHandlerFactory)(implicit system: ActorSystem): ActorRef = {
    system.actorOf (Props (classOf[Coordinator], factory), "Coordinator")
  }

  def apply ()(implicit system: ActorSystem): ActorRef = {
    apply (PatchHandler)(system)
  }

  def chooseDimensions (patchCount: Int): (Int, Int) = {
    var start = Math.sqrt (patchCount).toInt
    while (patchCount % start != 0) {start -= 1}
    (start, patchCount / start)
  }

  def chooseRectangles (patchWidth: Int, patchHeight: Int, cellWidth: Int, cellHeight: Int): List[(Point, Point)] = {
    val leftBorders = 0 :: (1 until patchWidth).map {idx => (idx * cellWidth) / patchWidth}.toList
    val rightBorders = leftBorders.tail.map (_ + 1) :+ cellWidth
    val topBorders = 0 :: (1 until patchHeight).map {idx => (idx * cellHeight) / patchHeight}.toList
    val bottomBorders = topBorders.tail.map (_ + 1) :+ cellHeight
    for (topBottom <- topBorders.zip (bottomBorders); leftRight <- leftBorders.zip (rightBorders))
      yield (Point (leftRight._1, topBottom._1), Point (leftRight._2, topBottom._2))
  }
}

class Coordinator (factory: PatchHandlerFactory) extends Actor {
  import Coordinator._

  private var client: ActorRef = null
  private var allPatchHandlers: List[ActorRef] = null
  private var generationsToGo: Int = 0
  private val runningPatchHandlers = scala.collection.mutable.Set[ActorRef] ()

  def receive = {
    case msg: Start => handleStart (msg.life, msg.patches, msg.generations)
    case msg: Complete => handleComplete ()
  }

  private def handleStart (lifeGrid: LifeGrid, patches: Int, generations: Int): Unit = {
    client = sender
    val (partsX, partsY) = chooseDimensions (patches)
    val rectangles = chooseRectangles (partsX, partsY, lifeGrid.width, lifeGrid.height)
    val neighborGrid = new NeighborGrid (lifeGrid.width, lifeGrid.height)
    allPatchHandlers = rectangles.map {rectangle =>
      factory (lifeGrid, neighborGrid, rectangle._1, rectangle._2)
    }
    generationsToGo = generations
    startNextGeneration ()
  }

  private def startNextGeneration (): Unit = {
    runningPatchHandlers.clear ()
    allPatchHandlers.foreach {handler =>
      handler ! Advance ()
      runningPatchHandlers.add (handler)
    }
  }

  private def handleComplete (): Unit = {
    runningPatchHandlers.remove (sender)
    if (runningPatchHandlers.isEmpty) {
      handleCompletedGeneration ()
      generationsToGo -= 1
      if (generationsToGo == 0) {
        client ! Finished ()
      }
      else {
        startNextGeneration ()
      }
    }
  }

  private def handleCompletedGeneration (): Unit = {

  }
}
