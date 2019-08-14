package com.org.ForComrehensions

object Test extends App{
  def foo(n: Int, v: Int) =
    for (i <- 0 until n; j <- 0 until n if i + j == v) yield (i,j)

  // Do foo method trả về một List of (Int, Int)
  foo(10, 10) foreach {
    case (i, j) => println(s"($i, $j)")
  }

  println("For comprehesions without yield statement")
  def foo2(n: Int, v: Int) = {
    for(i <- 0 until n;
        j <- 0 until n if i + j == v) {
      println(s"($i, $j)")
    }
  }
  foo2(10, 10)
}
