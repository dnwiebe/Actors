package cse.actors.life

import akka.actor.{Props, ActorRef, ActorSystem, Actor}

/**
  * Created by dnwiebe on 1/29/16.
  */

case class Advance ()
case class Complete ()

trait PatchHandlerFactory {
  def apply (life: LifeGrid, neighbors: NeighborGrid, upLeft: Point, loRight: Point): ActorRef
}

object PatchHandler extends PatchHandlerFactory {
  implicit var system: ActorSystem = null
  def apply (life: LifeGrid, neighbors: NeighborGrid, upLeft: Point, loRight: Point): ActorRef = {
    system.actorOf (Props (classOf[PatchHandler], life, neighbors, upLeft, loRight),
      s"PatchHandler_${upLeft.x},${upLeft.y}-${loRight.x},${loRight.y}")
  }
}

class PatchHandler (life: LifeGrid, neighbors: NeighborGrid, upLeft: Point, loRight: Point) extends Actor {
  private def OFFSETS = for (dx <- -1 to 1; dy <- -1 to 1; if (dx != 0) || (dy != 0)) yield Point (dx, dy)

  override def receive = {
    case _: Advance => advanceOneGeneration ()
  }

  private def advanceOneGeneration (): Unit = {
    updateNeighbors (life, neighbors)
    updateLife (life, neighbors)
    sender ! Complete ()
  }

  private def updateNeighbors (life: LifeGrid, neighbors: NeighborGrid): Unit = {
    neighbors.clear ()
    for (
      y <- upLeft.y + 1 until loRight.y - 1;
      x <- upLeft.x + 1 until loRight.x - 1;
      p = Point (x, y)
    ) if (life.isAliveAt (p)) {
      val neighborPoints = OFFSETS.map {o => Point (x + o.x, y + o.y)}
      neighborPoints.foreach (neighbors.addNeighbor)
    }
  }

  private def updateLife (life: LifeGrid, neighbors: NeighborGrid): Unit = {
    for (
      x <- upLeft.x + 1 until loRight.x - 1;
      y <- upLeft.y + 1 until loRight.y - 1;
      p = Point (x, y)
    ) {
      (life.isAliveAt (p), neighbors.neighborCount (p)) match {
        case (false, c) if c == 3 => life.registerLifeAt (p)
        case (true, c) if c < 2 => life.registerDeathAt (p)
        case (true, c) if c > 3 => life.registerDeathAt (p)
        case _ =>
      }
    }
  }
}
