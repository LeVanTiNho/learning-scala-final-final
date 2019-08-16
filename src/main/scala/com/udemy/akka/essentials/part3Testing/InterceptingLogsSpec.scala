package com.udemy.akka.essentials.part3Testing

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class InterceptingLogsSpec extends TestKit(ActorSystem("InterceptingLogsSpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll{

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import InterceptingLogsSpec._

  "A checkout flow" should {
    "correctly log the dispatch of an order" in {

      val item = "Rock the JVM Akka course"
      val creditCard = "1234-1234-1234-1234"

      // Trong nhiều trường hợp chúng ta không thể can thiệp sâu và actor hierarchy để test
      // Khi đó chúng ta có thể test dựa vào những logs mà các actor ghi ra

      // EventFilter to filter logs
      // Để EventFilter có thể catch logs chúng ta cần set akka.testkit.TestEventListener loger cho The ActorSystem of this testkit
      // To use re-gex to filter log messages, we need to pass in a Pattern
      EventFilter.info(pattern = s"Order [0-9]+ for item  $item has been dispatched", occurrences = 1) intercept {
        // our test code
        val checkoutRef = system.actorOf(Props[CheckoutActor])
        checkoutRef ! Checkout(item, creditCard)
      }
    }
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

  class FulfilmentManeger extends Actor with ActorLogging{
    // Ở đây ta có thể dùng context.become để chuyển đổi ngữ cảnh thay vì dùng variable
    var orderId = 43
    override def receive: Receive = {
      case DispatchOrder(item: String) =>
        orderId += 1

        log.info(s"Order $orderId for item $item has been dispatched")
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
