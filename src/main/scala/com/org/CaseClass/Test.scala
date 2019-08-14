package com.org.CaseClass

object Test extends App{
  val message1 = Message("guillaume@quebec.ca", "jorge@catalonia.es", "Ça va ?")
  val message2 = Message("jorge@catalonia.es", "guillaume@quebec.ca", "Com va?")
  val message3 = Message("jorge@catalonia.es", "guillaume@quebec.ca", "Com va?")
  println(message1 == message3)

  // Copying
  val message4 = Message("julien@bretagne.fr", "travis@washington.us", "Me zo o komz gant ma amezeg")
  val message5 = message4.copy()
}