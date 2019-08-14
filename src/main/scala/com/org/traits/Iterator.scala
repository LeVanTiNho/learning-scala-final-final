package com.org.traits

trait Iterator[A] {
  def hasNext: Boolean
  def next(): A
}
