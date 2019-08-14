package com.org.SingletonObjects.Logging

object Logger {
  def info(message: String): Unit = println(s"INFO: $message")
}
