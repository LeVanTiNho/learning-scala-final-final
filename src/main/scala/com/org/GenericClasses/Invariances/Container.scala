package com.org.GenericClasses.Invariances

import com.org.GenericClasses.Covariances.Test.{Animal, Cat}

class Container[A](value: A) {
  private var _value: A = value
  def getValue: A = _value
  def setValue(value: A) ={
    _value = value
  }
}

object Test extends App {
}
