package com.org.SingletonObjects

import com.org.SingletonObjects.Logging.Logger._
object Test extends App {

  // One use case of singleton objects
  val project1 = new Project("TPS Reports", 1)
  val project2 = new Project("Website redesign", 5)
  info("Created projects")


}
