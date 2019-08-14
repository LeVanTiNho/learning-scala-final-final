package com.org.GenericClasses.Contravariances

import com.org.GenericClasses.Covariances.Test.{Animal, Cat}

abstract class Printer[-A] {
  def print(value: A)
}

class AnimalPrinter extends Printer[Animal] {
  override def print(value: Animal): Unit = {
    println("The animal's name is: " + value.name)
  }
}

class CatPrinter extends Printer[Cat] {
  override def print(value: Cat): Unit = {
    println("The cat's name is: " + value.name)
  }
}

object Test extends App {
  val myCat = Cat("Boots")
  def printMyCat(printer: Printer[Cat]) = {
    printer.print((myCat))
  }

  val catPrinter: Printer[Cat] = new CatPrinter
  val animalPrinter: Printer[Animal] = new AnimalPrinter
  printMyCat(catPrinter)
  printMyCat(animalPrinter)
}