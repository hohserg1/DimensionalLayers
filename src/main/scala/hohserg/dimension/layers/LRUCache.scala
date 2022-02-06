package hohserg.dimension.layers

import java.util

class LRUCache[A, B](capacity: Int) extends util.LinkedHashMap[A, B](capacity, 0.7f, true) {
  override def removeEldestEntry(entry: util.Map.Entry[A, B]): Boolean = {
    this.size() > capacity
  }
}
