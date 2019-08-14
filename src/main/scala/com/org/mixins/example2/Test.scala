package com.org.mixins.example2

object Test extends App{
  class RichStringIterator(s: String) extends StringIterator(s) with RichIterator
  val richStringIterator = new RichStringIterator("Scala")
  richStringIterator foreach println
}
