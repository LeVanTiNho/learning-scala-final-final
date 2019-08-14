package com.udemy.akka.essentials.part4FaultTolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.udemy.akka.essentials.part4FaultTolerance.StartingStoppingActors.Parent.{StartChild, StopChild}

object StartingStoppingActors extends App {
  val system = ActorSystem("StoppingActorsDemo")

  object Parent {
    case class StartChild(name: String)
    case class StopChild(name: String)
    // Stop object used to stop a Parent Actor
    case object Stop
  }
  class Parent extends Actor with ActorLogging {
    import Parent._

    // Phương thức withChilden trả về một handler xử lý các thông điệp liên quan đến children của parent
    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name) =>
        log.info(s"Starting child $name")

        // children + (name -> context.actorOf(Props[Child], name)) sẽ được thực hiện trước
        // Sau đó withchildren sẽ được đẩy vào stack of Receive handlers
        context.become(withChildren(children + (name -> context.actorOf(Props[Child], name))))

      case StopChild(name) =>
        log.info(s"Stopping child with the name $name")
        val childOption = children.get(name)

        // context.stop thì asynchronously
        childOption.foreach(childRef => context.stop(childRef))
    }

    override def receive: Receive = {
      case Stop =>
        log.info("Stopping myself")

        // The stop method stops the child actors of the parent actor
        context.stop(self)
      case message: String => log.info(message)
      case _ => withChildren(Map())
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  // Method 1 - Using context.stop
  import Parent._
  val parent = system.actorOf(Props[Parent], "parent")
  //parent ! StartChild("child1")
  //val child = system.actorSelection("/user/parent/child1")
  //child ! "hi kid!"

  //parent ! StopChild("child1")
  //for(_ <- 1 to 50) child ! "are you still there?"

  parent ! StopChild("child2")
  val child2 = system.actorSelection("/user/parent/child2")
  child2 ! "hi, second child"

  parent ! Stop
  for (_ <- 1 to 10) parent ! "parent, are you still there?"
  for (i <- 1 to 100) child2 ! s"[$i] second kid, are you still alive?"
}