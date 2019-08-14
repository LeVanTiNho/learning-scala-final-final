package com.org.GenericClasses

object Test extends App{
  class A
  class B extends A
  val stack = new Stack[A]
  stack.push(new B)
  stack.push(new A)
}
