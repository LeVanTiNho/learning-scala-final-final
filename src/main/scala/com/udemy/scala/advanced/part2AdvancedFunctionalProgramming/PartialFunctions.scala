package com.udemy.scala.advanced.part2AdvancedFunctionalProgramming

class FunctionNotApplicableException extends RuntimeException

object PartialFunctions extends App{
  // Function1[Int, Int] === Int => Int
  val aFunction = (x: Int) => x + 1

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if(x == 2) 56
    else throw new FunctionNotApplicableException

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 76
    case 5 => 999
  }

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 75
    case 5 => 999
  } // Partial function value

  println(aPartialFunction(2))

  // Partial function utilities
  // isDefinedAt method test with a particular domain, the partial function have processing for it
  println(aPartialFunction.isDefinedAt(67))

  // lift transform a partial function into a total function with return of Option
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(98))

  // orElse to chain partial functions
  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }
  println("---------------------------------")
  println(pfChain(2))
  println(pfChain(45))

  // Partial functions extend normal function, so we can assign a partial function literal to a normal function val
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // Higher order functions accept partial functions as well
  // The map method takes an argument as a function, we can pass that function without parenthesis
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
}