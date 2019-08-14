package com.udemy.akka.essentials.part2Actors

import akka.actor.{Actor, ActorSystem, Props}

object AkkaCapabilities extends App{
  // Define a simple actor class
  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message: String => println(s"[simple actor] I have received $message")
    }
  }
  val system = ActorSystem("actorCapabilitiesDome")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")
  simpleActor ! "hello, actor"

  // 1 - messages can be of any type
  // Messages must meet two conditions
  // a) messages must be IMMUTABLE
  // b) messages must be SERIALIZABLE
  // 99.99% we use case classes to models messages of many types

}
