package cse.actors.life

/**
  * Created by dnwiebe on 1/29/16.
  */
class NeighborGrid (val width: Int, val height: Int) extends Bordered {

  private val longs :Array[Array[Long]] = (1 to (width >> 4) + 1).map {_ => new Array[Long](height)}.toArray

  def addNeighbor (point: Point): Unit = {
    validatePointInGrid (point, "add neighbor past")
    val longColumn = point.x >> 4
    val currentLongValue = longs(longColumn)(point.y)
    val shift = (point.x & 0xF) << 2
    val mask = 0xFL << shift
    val currentNeighborCount = currentLongValue & mask
    val nextNeighborCount = currentNeighborCount + (1L << shift)
    val nextLongValue = (currentLongValue & ~mask) | nextNeighborCount
    longs(longColumn)(point.y) = nextLongValue
  }

  def neighborCount (point: Point): Int = {
    validatePointInGrid (point, "get count past")
    val longColumn = point.x >> 4
    val currentLongValue = longs(longColumn)(point.y)
    val shift = (point.x & 0xF) << 2
    val mask = 0xFL << shift
    val currentNeighborCount = currentLongValue & mask
    (currentNeighborCount >> shift).toInt
  }

  def clear (): Unit = {
    longs.foreach {array => array.indices.foreach {i => array(i) = 0}}
  }

  override def toString: String = {
    (0 until height).map {y =>
      (0 until width).map {x =>
        s"${neighborCount (Point (x, y))}  "
      }.mkString
    }.mkString ("\n")
  }
}
