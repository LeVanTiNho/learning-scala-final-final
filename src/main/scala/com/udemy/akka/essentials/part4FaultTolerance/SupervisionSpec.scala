package com.udemy.akka.essentials.part4FaultTolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SupervisionSpec extends TestKit(ActorSystem("SupervisionSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll{
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import SupervisionSpec._

  "A supervior" should {
    // To test in case of RuntimeException
    "resume its child in case of a minor fault" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]
      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      child ! "Akka is awesome because I am learning to think in a whole new way"
      child ! Report
      expectMsg(3)
    }

    // To test in case of NullPointerException

    // To test Stop strategy
    "terminate its child in case of a major error" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! "akka is nice"
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child)
    }
  }
}

object SupervisionSpec {

  /**
    * By default, the supervisor's strategy is restarting the child actor if it throws a exception
    * Thực chất việc restarting một actor là replacing a old actor instance bằng a new actor instance
    */
  class Supervisor extends Actor {

    // To change the default behavior of Supervisor when its children throw exceptions
    override val supervisorStrategy = OneForOneStrategy(){
      // Restart, Stop, Resume, Escalate is Directives (chỉ thị)
      // A Directive ra chỉ dẫn cho Actor xử lý khi con của nó gặp lỗi như có exception
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate
    }

    override def receive: Receive = {
      // A Props is used to create a actor
      case props: Props =>
        val childRef = context.actorOf(props)
        sender() ! childRef
    }
  }

  case object Report
  class FussyWordCounter extends Actor {
    private var words = 0
    override def receive: Receive = {
      case Report => sender() ! words
      case "" => throw new NullPointerException("sentence in empty")
      case sentence: String =>
        if(sentence.length > 20) throw new RuntimeException("sentence is too big")
        else if(!Character.isUpperCase(sentence(0))) throw new IllegalArgumentException("sentence must start with upper charater")
        else words += sentence.split(" ").length
      case _ => throw new Exception("can only receive strings")
    }
  }
}
