package com.org.GenericClasses.UpperTypeBounds

object UpperTypeBoundExample extends App{
  abstract class Animal {
    def name: String
  }
  abstract class Pet extends Animal
  class Cat extends Pet {
    override def name: String = "Cat"
  }
  class Dog extends Pet {
    override def name: String = "Dog"
  }
  class Lion extends Animal {
    override def name: String = "Lion"
  }

  // P type parameter has a upper bound
  class PetContainer[P <: Pet](p: P) {
    def pet: P = p
  }
}
