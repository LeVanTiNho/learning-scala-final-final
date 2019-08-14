package com.udemy.akka.essentials.part2Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object IntroAkkaConfig extends App{
  // Configuration controls the behavior of Akka, the entire configuration is held withing an actor system
  // A configuration is pair of name and value, we create a file to store configurations

  class SimpleLoggingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  // 1 - Inline configuration, using a string to create configs
  val configString =
    """
      |akka {
      | loglevel = "ERROR"
      | }
    """.stripMargin

  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("ConfigurationDemo", ConfigFactory.load(config))
  val actor = system.actorOf(Props[SimpleLoggingActor])


  // 1 - Configurations from application.conf file
  // application.conf file is the default file for Akka look for configs
  val defaultConfigFileSystem = ActorSystem("DefaultConfigFileDemo")
  val defaultConfigActor = defaultConfigFileSystem.actorOf(Props[SimpleLoggingActor])
  defaultConfigActor ! "Remember me"


}