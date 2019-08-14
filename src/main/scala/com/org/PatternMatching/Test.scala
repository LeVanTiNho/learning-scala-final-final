package com.org.PatternMatching

import scala.util.Random

object Test extends App {
  // Systax
  val x: Int = Random.nextInt()

  val s = x match {
    case 0 => "zero"
    case _ => "other"
  }

  // Matching on case classes extend a class/trait
  abstract class Notification
  case class Email(sender: String, title: String, body: String) extends Notification
  case class SMS(caller: String, message: String) extends Notification
  case class VoiceRecording(contactName: String, link: String) extends Notification

  def showNotification(notification: Notification): String = notification match {
    case Email(sender, title, _) => s"You got an email from $sender with title $title"
    case SMS(number, message) => s"You got an SMS from $number! Message: $message"
    case VoiceRecording(name, link) =>  s"You received a Voice Recording from $name! Click the link to hear it: $link"
  }

  val someSms = SMS("122345", "Are you there?")
  val someVoiceRecording = VoiceRecording("Tom", "voicerecording.org/id/123")
  println(showNotification(someSms))
  println(showNotification(someVoiceRecording))

  // Pattern matching on a type extending a common
  abstract class Device
  case class Phone(model: String) extends Device {
    def screenOff = "Turning screen off"
  }
  case class Computer(model: String) extends Device {
    def screenSaverOn = "Turning screen saver on..."
  }

  def goIdle(device: Device) = device match {
    case p: Phone => p.screenOff
    case c: Computer => c.screenSaverOn
  }
}
