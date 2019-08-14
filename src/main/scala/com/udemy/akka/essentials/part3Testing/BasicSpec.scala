package com.udemy.akka.essentials.part3Testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActor, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._

// Đặc tên cho testing class có hậu tố là Spec
// An Actorsystem is created and it's a part of a TestKit
// BeforeAndAfterAll supply some hooks so that when you run your test suite, the set hooks will be called
class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll{

  // testActor is the sender of messages and the receiving end of that messages.
  // testActor communicates with the actors we want to test
  // testActor is member of testkit
  // testActor is passed implicitly as a sender with every message sent to the actor
  // ImplicitSender trait passes implicitly the testActor
  testActor

  // This method from BeforeAndAfterAll trait, for destroying the test suite
  // We will terminate the actor system here
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  // The general structure of a test
  "The thing being tested" should {
    "do this" in {
      // testing scenario
    }
  }

  import BasicSpec._

  "A simple actor" should {
    "send back the same message" in {
      val echoActor = system.actorOf(Props[SimpleActor])
      val message = "hello, test"
      echoActor ! message

      // akka.test.single-expect-default -> default timeout
      expectMsg(message)
    }
  }

  "A blackhold actor" should {
    "send back some message" in {
      val blackHole = system.actorOf(Props[BlackHole])
      val message = "hello, test"
      blackHole ! message

      // expectMsg(message)

      // We need to import scala.concurrent.duration._ to can use 'second' unit
      expectNoMessage(1 second)
    }
  }

  // Message assertions
  "A lab test actor" should {
    val labTestActor = system.actorOf(Props[LabTestActor])
    "turn a string into uppercase" in {
      labTestActor ! "I love Akka"
      val reply = expectMsgType[String]
      assert(reply == "I LOVE AKKA")
    }
  }
}

object BasicSpec{
  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => sender() ! message
    }
  }

  class BlackHole extends Actor {
    override def receive: Receive = Actor.emptyBehavior
  }

  class LabTestActor extends Actor {
    override def receive: Receive = {
      case message: String => sender() ! message.toUpperCase
    }
  }
}