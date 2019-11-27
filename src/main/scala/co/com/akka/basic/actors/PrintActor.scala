package co.com.akka.basic.actors

import akka.actor.Actor

class PrintActor extends Actor {


  override def receive: Receive = {
    case message:String =>
      println(s"Got message: $message")
  }
}
