package com.udemy.akka.essentials.part5Infrastructure

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}

import scala.concurrent.duration._

/*
We use Scheduler and Timer to make the actor able to run a particular piece of code at
a defined point in the future and able to repeat
 */
object TimersSchedulers extends App {

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("SchedulersTimerDemo")
  val simpleActor = system.actorOf(Props[SimpleActor])

  // A ActorSystem object has its own logger
  system.log.info("Scheduling reminder for simpleActor")

  // The scheduling of code must happen on some kind thread
  // the scheduler need a ExecutionContest to work
  // we can pass it implicitly, explicitly, or import system.dispatcher
  // dispatcher is a Execution Contest
  import system.dispatcher

  // A scheduler schedules actions will be done by an actor

  // The scheduler of The ActorSystem can schedule a piece of code run once
  system.scheduler.scheduleOnce(1 second){
    simpleActor ! "reminder"
  }

  /*
  Scheduling a piece of code run repeatedly
   */
  val routine: Cancellable = system.scheduler.schedule(1 second, 2 seconds) {
    simpleActor ! "heatbeat"
  }

  system.scheduler.scheduleOnce(5 seconds) {
    routine.cancel
  }

  /*
   Things to bear in mind:
    - Don't use unstable references inside scheduled actions
    - All scheduled tasks execute when the system terminated regardless of the initial delay
    - Schedules are not really millisecond precise and they aren't useable for long term
   */

}
