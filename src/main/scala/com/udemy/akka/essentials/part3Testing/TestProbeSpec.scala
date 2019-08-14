package com.udemy.akka.essentials.part3Testing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class TestProbeSpec extends TestKit(ActorSystem("TestProbeSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {
  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TestProbeSpec._

  "A master actor" should {
    "register a slave" in {
      val master = system.actorOf(Props[Master])
      // A TestProbe is a TestKit,it has a fictitious actor, using for testing a particular actor
      // A TestProbe has the same assertion capabilities as the general TestKit
      val slave = TestProbe("slave")
      // slave.ref return the actor of the TestProbe
      master ! Register(slave.ref)
      expectMsg(RegistrationAck)
      val workloadString = "I love Akka"
      master ! Work(workloadString)

      // Testing the interaction between the master and the slave actor
      slave.expectMsg(SlaveWork(workloadString, testActor))

      // The nicest thing about TestProbes is that they can be instructed to send messages
      slave.reply(WorkCompleted(3, testActor))

      expectMsg(Report(3))
    }
  }

  "aggregate date correctly" in {
    val master = system.actorOf(Props[Master])
    val slave = TestProbe("slaveTestProbe")
    master ! Register(slave.ref)
    expectMsg(RegistrationAck)
    val workLoadString = "I love Akka"
    master ! Work(workLoadString)
    master ! Work(workLoadString)
    // Phương thức receiveWhile của TestProbe ta cần truyền vào a PartialFunction, PartialFunction là Message handler cho actor thành viên của TestProbe
    slave.receiveWhile(){
      case SlaveWork(workLoadString, testActor) => slave.reply(WorkCompleted(3, testActor))
    }
    expectMsg(Report(3))
    expectMsg(Report(6))
  }
}

object TestProbeSpec {
  // Scenario
  /*
  Word counting actor hierarchy master-slave
  The flow of scenario
    - Send some work to the master
    - Master send the slave the piece of work
    - Slave processes the work and replies to master
    - Master sends the total count to the original requester
   */

  class Master extends Actor {

    def online(slaveRef: ActorRef, totalWordCount: Int): Receive = {
      // Một requester yêu cầu Master làm việc, master sẽ yêu cầu slave làm việc
      // Cần chuyền cho slaveRef sender vì ta cần biết original requester là ai
      case Work(text) => slaveRef ! SlaveWork(text, sender)

      case WorkCompleted(count, originalRequester) =>
        val newTotalWordCount = totalWordCount + count
        originalRequester ! Report(newTotalWordCount)
        // Sau khi tính toán hoàng tất, online sẽ bị đưa ra khỏi stack of the receive handlers
        // Nên để tiếp tục tính toàn tà cần đưa online vào the stack lần nữa
        context.become(online(slaveRef, newTotalWordCount))
    }

    override def receive: Receive = {
      // Sau khi đăng kí xong slaveRef -> chuyển đổi ngữ cảnh sang online
      case Register(slaveRef) =>
        sender ! RegistrationAck
        context.become(online(slaveRef, 0))
    }
  }

  case class Register(slavaRef: ActorRef)
  case class Work(text: String)
  case class SlaveWork(text: String, originalRequester: ActorRef)
  case class WorkCompleted(conut: Int, originalRequester: ActorRef)
  case class Report(newTotalWordCount: Int)
  object RegistrationAck
}