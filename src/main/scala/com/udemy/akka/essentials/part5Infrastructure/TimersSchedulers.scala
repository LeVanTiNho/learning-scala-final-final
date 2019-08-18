package com.udemy.akka.essentials.part5Infrastructure

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props, Timers}

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

/*

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


 */


  /*
   Things to bear in mind:
    - Don't use unstable references inside scheduled actions
    - All scheduled tasks execute when the system terminated regardless of the initial delay
    - Schedules are not really millisecond precise and they aren't useable for long term
   */

  /**
    * Exercise: Implement a self-closing actor
    *   - If the actor receive a message (anything), you have 1 second to send it another message
    *   - If the window expires, the actor will stop itself!
    *   - If you send another message, the time window is reset
    */
  /*
  import system.dispatcher
  class SelfClosingActor extends Actor with ActorLogging {
    var schedule: Cancellable = createTimeoutWindow

    def createTimeoutWindow: Cancellable = {
      context.system.scheduler.scheduleOnce(1 second) {
        self ! "timeout"
      }
    }

    override def receive: Receive = {
      case "timeout" =>
        // stop itself
        log.info("Stopping myself")
        context.stop(self)
      case message =>
        log.info(s"Received $message, staying alive")
        schedule.cancel
        schedule = createTimeoutWindow
    }
  }

  val selfClosingActor = system.actorOf(Props[SelfClosingActor], "selfClosingActor")
  system.scheduler.scheduleOnce(250 millis) {
    selfClosingActor ! "ping"
  }

  system.scheduler.scheduleOnce(2 seconds) {
    system.log.info("sending pong to the self-closing actor")
    selfClosingActor ! "pong"
  }
  /

   */

  /**
    * Scheduler khó kiểm soát khi actor đã stoped hay restarted
    * Đối với trường hợp ta cần scheduling messages để gửi bên trong một actor thì da dùng timer
    */

  case object TimerKey
  case object Start
  case object Reminder
  case object Stop

  class TimerBasedHeartbeatActor extends Actor with ActorLogging with Timers {
    // Mỗi một timer cần một key có kiểu object
    // singleTimer là timer chỉ gửi message một lần
    timers.startSingleTimer(TimerKey, Start, 500 millis)

    override def receive: Receive = {
      case Start =>
        log.info("Bootstrapping")

        // startPeriodicTimer -> gửi messages theo chu kì
        // khi ta gọi startPeriodTimer với key là TimerKey thì timer đang giữ key này sẽ bị cancel
        timers.startPeriodicTimer(TimerKey, Reminder, 1 second)
      case Reminder =>
        log.info("I am alive")
      case Stop =>
        log.warning("Stopping!")
        timers.cancel(TimerKey)
        context.stop(context.self)
    }

    import system.dispatcher
    val timerBasedHeartbeatActor = system.actorOf(Props[TimerBasedHeartbeatActor], "timerActor")
    system.scheduler.scheduleOnce(5 seconds) {
      timerBasedHeartbeatActor ! Stop
    }
  }
}
