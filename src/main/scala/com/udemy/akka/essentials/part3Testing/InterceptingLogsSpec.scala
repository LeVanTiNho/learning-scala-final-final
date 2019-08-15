package com.udemy.akka.essentials.part3Testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class InterceptingLogsSpec extends TestKit(ActorSystem("InterceptingLogsSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll{

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

}

object InterceptingLogsSpec {

  class CheckoutActor extends Actor {
    private val paymentManager = context.actorOf(Props[PaymentManager])
    private val fulfillmentManager = context.actorOf(Props[FulfilmentManeger])

    override def receive: Receive = ???

    def awaitingCheckout: Receive = {
      case Checkout(item, card) =>
        paymentManager ! AuthorizeCard(card)

        // pendingPayment đợi xử lý xong
        context.become(pendingPayment(item))
    }

    def pendingPayment(item: String): Receive = {
      case PaymentAccepted =>
        fulfillmentManager ! DispatchOrder(item)
        context.become(pendingFulfilment(item))
      case PaymentDenied => //TODO
    }

    def pendingFulfilment(item: String): Receive = {
      case OrderConfirmed => context.become(awaitingCheckout)
    }
  }

  class PaymentManager extends Actor {
    override def receive: Receive = {
      case AuthorizeCard(card) =>
        if (card.startsWith("0")) sender ! PaymentDenied
        else sender ! PaymentAccepted
    }
  }

  class FulfilmentManeger extends Actor {
    // Ở đây ta có thể dùng context.become để chuyển đổi ngữ cảnh thay vì dùng variable
    var orderId = 43
    override def receive: Receive = {
      case DispatchOrder(item: String) =>
        orderId += 1
        sender ! OrderConfirmed
    }
  }

  // Define messages by which the Actors communicate.
  case class Checkout(item: String, card: String)

  case class AuthorizeCard(card: String)

  case object PaymentDenied

  case object PaymentAccepted

  case class DispatchOrder(item: String)

  case object OrderConfirmed
}
