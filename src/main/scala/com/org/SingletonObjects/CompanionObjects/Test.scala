package com.org.SingletonObjects.CompanionObjects
import scala.math._

object Test extends App {
  case class Cirle(radius: Double) {
    import Cirle._
    def area: Double = calculateArea(radius)
  }

  object Cirle {
    private def calculateArea(radius: Double): Double = Pi * pow(radius, 2)
  }

  val circle1 = new Cirle(5.0)
  println(circle1.area)
}
