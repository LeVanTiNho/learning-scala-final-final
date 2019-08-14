package com.udemy.akka.essentials.part3Testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import scala.concurrent.duration._
import scala.util.Random

class TimeAssertionSpec extends TestKit(ActorSystem("TimedAssertionSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll{
  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
   import TimedAssertionSpec._

  "A worker actor" should {
    val workerActor = system.actorOf(Props[WorkerActor])
    "reply with the meaning of life in a timely manner" in {
      // A time box test
      // The code in within method must happen at least 500 milliseconds, at most 1 second
      within(500 millis, 1000 millis) {
        workerActor ! "work"
        expectMsg(WorkResult(42))
      }
    }

    "reply with valid work at a reasonable cadence" in {
      workerActor ! "workSequence"

      // Trả về a seq of Int, max duration là 2 seconds, max idle time giữa các messages liên tiếp là 500 millies
      val results = receiveWhile[Int](max = 2 seconds, idle = 10 millis, messages = 10) {
        case WorkResult(result) => result
      }

      assert(results.sum > 5)
    }
  }
}

object TimedAssertionSpec {
  // Testing scenario

  case class WorkResult(result: Int)

  class WorkerActor extends Actor {
    override def receive: Receive = {
      case "work" =>
        // long computation
        Thread.sleep(100)
        sender ! WorkResult(42)
      case "workSequence" =>
        // The WorkerActor will send back a sequence of WorkResult to the sender, so we use the receiveWith method to test this case
        val r = new Random
        for(_ <- 1 to 10) {
          Thread.sleep(r.nextInt(1))
          sender ! WorkResult(1)
        }
    }
  }
}