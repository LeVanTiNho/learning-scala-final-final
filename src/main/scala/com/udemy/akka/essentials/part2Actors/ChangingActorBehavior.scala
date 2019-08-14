package com.udemy.akka.essentials.part2Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.udemy.akka.essentials.part2Actors.ChangingActorBehavior.Mom.MomStart

object ChangingActorBehavior extends App{
  // change behavior with time by keeping track of the current state of the the actor
  object FussyKid {
    // Represents the response to the Ask message from Moms
    case object KidAccept
    case object KidReject

    // Represents the types of state of the kid
    val HAPPY = "happy"
    val SAD = "sad"
  }

  class FussyKid extends Actor {
    // Import utilities from the FussyKid companion object
    import FussyKid._

    // Import the types of food, Food messages from Mom companion object
    import Mom.{CHOCOLATE, VEGETABLE, Food, Ask}

    // Internal state of the kid
    var state = HAPPY

    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        if(state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }

  class StatelessFussyKid extends Actor {
    import FussyKid._
    import Mom._

    /*
    The receive method of Actor trait return a Receive object, Receive type is alias of PartialFunction[Any, Unit].
    The actor system will call this partial function to handle messages
     */
    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLE) =>
        // Change my receive handler to sadReceive
        // We use context.become method
        // The second parameter of the context.become method indicates that the new message handler will be pushed to the top of the stack of handlers or not.
        context.become(sadReceive, false)
      case Food(CHOCOLATE) =>
        // Stay happy

      case Ask(_) => sender ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) =>
        // Stay sad

        // If the kid actor receive a more food(vege) message, then the kid will become more sad
        context.become(sadReceive, false)

      case Food(CHOCOLATE) =>
        // Change my receive handler to happyReceive
        // Use context.become
        // context.become(happyReceive, false)

        // If the kid actor receive a food(choco) message, then the kid will become more happy
        // unbecome method pop a message handler out of the top of the stack
        context.unbecome()
      case Ask(_) => sender ! KidAccept
    }
  }

  // It is a good practice to put case classes that models messages sent by Mom in the Mom companion object
  object Mom {
    // Represent the feeding action of the mom with a particular food type
    case class Food(food: String)

    // Represent the the ask action of the mom with a particular ask
    case class Ask(message: String)

    // Represent the message, which we make to make a mom interact with her kid
    case class MomStart(kidRef: ActorRef)

    // Represents the types of food
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }

  class Mom extends Actor {
    // Import everything from The Mom companion
    import Mom._

    // Import everything from The FussyKid companion
    import FussyKid._

    override def receive: Receive = {
      case MomStart(kidRef) =>
        // Test our interaction
        // A pair of a mom sending messages to a kid will guarantees the order of the messages
        // Make a mom give food to her kid
        kidRef ! Food(VEGETABLE)
        // Make a mom ask her kid
        kidRef ! Ask("Do you want to play?")
      case KidAccept => println("Yay, my kid is happy!")
      case KidReject => println("My kid is sad, but as he's healthy!")
    }
  }

  val system = ActorSystem("chaningActorBehaviorDemo")
  val fussyKid = system.actorOf(Props[FussyKid])
  val mom = system.actorOf(Props[Mom])

  // We make the mom actor start its action
  mom ! MomStart(fussyKid)

  /*
  In case, context.become is set discardOld = false
  Food(veg) -> stack.push(sadReceive)
  Food(chocolate) -> stack.push(happyReceive)

  Stack:
    happyReceive_2
    sadReceive_1
    happyReceiv_1
   */

  /*
  context.unbecome
  Food(veg)
  Food(veg)
  Food(choco)

  Stack:
    sad_1
    happy_1
   */

  /*
  Rules:
    + Akka always uses the latest handler on top of the stack
    + If the stack is empty, it call receive
   */
}
