package com.org.traits

import scala.collection.mutable.ArrayBuffer

object Test extends App {
  // Using a trait
  val iterator = new IntIterator(10);
  while (iterator.hasNext)
    println(iterator.next)

  // Subtyping
  val dog = new Dog("Harry")
  val cat = new Cat("Sally")

  val animals = ArrayBuffer.empty[Pet]

  animals.append(dog)
  animals.append(cat)
  animals.foreach(pet => println(pet.name))
}
