package com.org.mixins

trait C extends A{
  // C will derive the message member from A
  // Any class extending A can use the message member
  def loudMessage = message.toUpperCase
}
