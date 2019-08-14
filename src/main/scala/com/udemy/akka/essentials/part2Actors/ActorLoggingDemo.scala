package com.udemy.akka.essentials.part2Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object ActorLoggingDemo extends App{
  // 1 - explicit logging
  class SimpleActorWithExplicitLogger extends Actor {
    val logger = Logging(context.system, this)

    override def receive: Receive = {
      /*
      There is 4 levels of a log:
      1 - DEBUG
      2 - INFO
      3 - WARNING/WARN
      4 - ERROR
       */
      case message =>  logger.info(message.toString)// LOG it
    }
  }

  // 2 - ActorLogging
  class ActorWithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("LoggingDemo")
  val actor = system.actorOf(Props[SimpleActorWithExplicitLogger])

  actor ! "Logging a simple message"
}