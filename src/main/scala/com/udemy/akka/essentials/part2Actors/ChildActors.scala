package com.udemy.akka.essentials.part2Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.udemy.akka.essentials.part2Actors.ChildActors.CreditCard.AttachToAccount

object ChildActors extends App{
  // Actors can create other actors using actor context

  // The version 1: Using mutable variable
  /*
  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }
  class Parent extends Actor {
    import Parent._

    var child: ActorRef = null

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"[${self.path}] creating child")

        // create a new actor right here using context.actorOf
        val chilRef = context.actorOf(Props[Child], name)
        child = chilRef
      case TellChild(message) =>
        if(child != null) child forward message
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"[${self.path}] I got: $message")
    }
  }
  */

  // Version 2 - convert to immutable
  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }
  class Parent extends Actor {
    import Parent._

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"[${self.path}] creating child")

        // create a new actor right here using context.actorOf
        val chilRef = context.actorOf(Props[Child], name)
        context.become(withChild(chilRef))
    }

    def withChild(childRef: ActorRef): Receive = {
      case TellChild(message) =>
        if(childRef != null) childRef forward message
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message: String => println(s"[${self.path}] I got: $message")
    }
  }

  import com.udemy.akka.essentials.part2Actors.ChildActors.Parent.{CreateChild, TellChild}

  val system = ActorSystem("ParentChildDemo")
  val parent = system.actorOf(Props[Parent], "parent")
  parent ! CreateChild("child")
  parent ! TellChild("hey kid")

  // Actor hierarchies
  // parent -> child -> grandChild
  //        -> child2

  /*
  Guardian actors (top-level)
  - /sytem = system guardian
  - /user = user-level guardian: where contains our actors created by actorOf
  - / = the root guardian
   */

  // Actor selection
  // find actors by path
  // actorSelection returns a ActorSelection object, it's a wrapper of the actorRef object
  val childSelection = system.actorSelection("/user/parent/child")
  childSelection ! "I found you!"

  /*
  Danger!
  Never pass mutable actor state, or the 'this' reference, to child actors
   */
  // Example
  object NaiveBankAccount {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object InitializeAccount
  }

  class NaiveBankAccount extends Actor {
    import NaiveBankAccount._
    import CreditCard._

    var amount = 0

    override def receive: Receive = {
      case InitializeAccount =>
        val creditCardRef = context.actorOf(Props[CreditCard], "card")
        creditCardRef ! AttachToAccount(this) // !!
      case Deposit(funds) => deposit(funds)
      case Withdraw(funds) => withdraw(funds)
    }

    // The problem comes from here, the deposit and withdraw method can be accessed from the outside
    def deposit(funds: Int) = amount += funds
    def withdraw(funds: Int): Unit = amount -= funds
  }

  object CreditCard {
    case class AttachToAccount(bankAccount: NaiveBankAccount)
    case object CheckStatus
  }
  class CreditCard extends Actor {
    import CreditCard._

    def attachedTo(account: NaiveBankAccount): Receive = {
      case CheckStatus =>
        println(s"[${self.path}] your message has been processed")

        // Here is the problem
        // Inside Credit class, we can call the withdraw method, that change the state of a particular NaiBankActor actor, it's extremely unsecured
        /*
        NaiveBankAccount class không giới hạng tầm vực của 2 method quan trọng là Deposit và Withdraw,
        nên khi nếu chúng ta truyền trực tiếp A NaviBankAccount object cho một method thì trong method đó có thể gọi 2 phương thức này.

        We should follow the rule of Akka Actor - Every actor must communicate with each other through message
         */
        account.withdraw(1)
    }

    override def receive: Receive = {
      case AttachToAccount(account) => context.become(attachedTo(account))
    }
  }

  import NaiveBankAccount._
  import CreditCard._
  val bankAccountRef = system.actorOf(Props[NaiveBankAccount], "account")
  bankAccountRef ! InitializeAccount
  bankAccountRef ! Deposit(100)
  bankAccountRef ! Withdraw(1)

  Thread.sleep(500)
  val ccSelection = system.actorSelection("/user/account/card")
  ccSelection ! CheckStatus
}
