package com.udemy.akka.essentials.part1Recap

import scala.concurrent.Future

object AdvancedRecap extends App{
  // Implicit conversions
  // 1) Implicit defs
  case class Person(name: String){
    def greet = s"Hi, my name is $name"
  }
  implicit def fromStringToPerson(string: String): Person = Person(string)
  "Peter".greet
  // fromStringToPerson("Peter").greet

  // 2) Implicit classes
  implicit class Dog(name: String) {
    def bark = println("bark!")
  }
  "Lassie".bark
  // new Dog("Lassie").bark

  // organize
  // local scope
  // imported scope
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future {
    println("hello, future")
  }

  // companion object of the types included in the call
}
