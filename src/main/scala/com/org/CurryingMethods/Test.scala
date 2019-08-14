package com.org.CurryingMethods

object Test extends App {
  // foldLeft method
  val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 17)
  val res = numbers.foldLeft(0)((m, n) => m + n)
  println(res)
}
