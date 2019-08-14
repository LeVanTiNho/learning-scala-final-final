package com.org.mixins.example2

abstract class AbsIterator {
  type T
  def hasNext: Boolean
  def next: T
}
