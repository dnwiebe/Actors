package cse.actors.life

/**
  * Created by dnwiebe on 1/30/16.
  */
trait Bordered {
  protected val width: Int
  protected val height: Int

  protected def validatePointInGrid (point: Point, target: String): Unit = {
    if (isInsideBorder (point, 0)) {
      throw new IllegalArgumentException (s"Cannot ${target} ${width}x${height} grid edge at ${point}")
    }
  }

  protected def validatePointInsideBorder (point: Point, target: String): Unit = {
    if (isInsideBorder (point, 1)) {
      throw new IllegalArgumentException (s"Cannot ${target} ${width}x${height} grid edge at ${point}")
    }
  }

  private def isInsideBorder (point: Point, borderWidth: Int): Boolean = {
    point.x < borderWidth ||
      point.y < borderWidth ||
      width - point.x <= borderWidth ||
      height - point.y <= borderWidth
  }
}
