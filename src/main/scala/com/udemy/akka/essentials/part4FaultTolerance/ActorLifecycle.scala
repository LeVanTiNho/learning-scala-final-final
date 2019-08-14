package com.udemy.akka.essentials.part4FaultTolerance

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}

object ActorLifecycle extends App{

  /**
    * preStart
    * postStop
    */
  object LifecycleActor {
    object StartChild
  }
  class LifecycleActor extends Actor with ActorLogging {
    import LifecycleActor._

    override def preStart(): Unit = log.info("I am starting")
    override def postStop(): Unit = log.info("I have stopped")

    override def receive: Receive = {
      case StartChild =>
        context.actorOf(Props[LifecycleActor], "child")
    }
  }

  import com.udemy.akka.essentials.part4FaultTolerance.ActorLifecycle.LifecycleActor.StartChild
  val system = ActorSystem("LifecycleDemo")
  //val parent = system.actorOf(Props[LifecycleActor], "parent")
  //parent ! StartChild

  // PoisonPill is a predefined message to stop a particular actor
  //parent ! PoisonPill

  /**
    * Restart
    */

  object FailChild
  object CheckChild
  class Parent extends Actor {
    private val child = context.actorOf(Props[Child], "supervisedChild")
    override def receive: Receive = {
      case FailChild => child ! Fail
      case CheckChild => child ! Check
    }
  }

  object Fail
  object Check
  class Child extends Actor with ActorLogging {

    override def preStart(): Unit = log.info("supervised child started")
    override def postStop(): Unit = log.info("supervised child stopped")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log.info(s"supervised actor restarting because of ${reason.getMessage}")

    override def postRestart(reason: Throwable): Unit =
      log.info("supervised actor restarted")

    override def receive: Receive = {
      case Fail =>
        log.warning("child will fail now")
        throw new RuntimeException("I failed")
      case Check =>
        log.info("alive and kicking")
    }
  }

  val supervisor = system.actorOf(Props[Parent], "supervisor")
  supervisor ! FailChild
  supervisor ! CheckChild

  /**
    * Default supervision strategy:
    * In a particular actor, a message which caused the exception to be thrown is removed from the queue
    * and not put back in the mailbox again,
    * and the actor will be restarted, the mailbox is still there, the next message will be processed.
    * Actor instance và mailbox tách biệt
    */
}
