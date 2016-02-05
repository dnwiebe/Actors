package cse.actors.life

/**
  * Created by dnwiebe on 1/29/16.
  */
class LifeGrid (val width: Int, val height: Int) extends Bordered {

  private val longs :Array[Array[Long]] = (1 to (width >> 6) + 1).map {_ => new Array[Long](height)}.toArray

  def registerLifeAt (point: Point): Unit = {
    validatePointInsideBorder (point, "register life on")
    val longColumn = point.x >> 6
    val currentValue = longs(longColumn)(point.y)
    val mask = 1L << (point.x & 0x3F)
    val newValue = currentValue | mask
    longs(longColumn)(point.y) = newValue
  }

  def registerDeathAt (point: Point): Unit = {
    validatePointInsideBorder (point, "register death on")
    val longColumn = point.x >> 6
    val currentValue = longs(longColumn)(point.y)
    val mask = ~(1L << (point.x & 0x3F))
    val newValue = currentValue & mask
    longs(longColumn)(point.y) = newValue
  }

  def isAliveAt (point: Point): Boolean = {
    validatePointInGrid (point, "query life past")
    val longColumn = point.x >> 6
    val currentValue = longs(longColumn)(point.y)
    val mask = 1L << (point.x & 0x3F)
    (currentValue & mask) != 0L
  }

  def clear (): Unit = {
    longs.foreach {array => array.indices.foreach {i => array(i) = 0L}}
  }

  override def toString: String = {
    (0 until height).map {y =>
      (0 until width).map {x =>
        if (isAliveAt (Point (x, y))) "X  " else "-  "
      }.mkString
    }.mkString ("\n")
  }
}
