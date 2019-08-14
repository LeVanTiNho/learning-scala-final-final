package com.org.ExtratorObjects

import scala.util.Random

object Test extends App {
  object CustomerID {
    def apply(name: String) = s"$name--${Random.nextLong}"

    def unapply(customerID: String): Option[String] = {
      val stringArray: Array[String] = customerID.split("--")
      if(stringArray.tail.nonEmpty) Some(stringArray.head) else None
    }
  }

  // CustomerID("Sukyoung) is shorthand syntax for CustomerID.apply("Sukyoung")
  val customer1ID = CustomerID("Sukyoung")
  customer1ID match {
      // unapply method will be called, the returned value will be assigned to the name
    case CustomerID(name) => println(name)
    case _ => println("Could not extract a CustomerID")
  }
}
