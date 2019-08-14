package com.org.CompoundTypes

object Test extends App {
  def cloneAndRet(obj: Cloneable with Resetable): Any = {
    val cloned = obj.clone()
    obj.reset
    cloned
  }
}
