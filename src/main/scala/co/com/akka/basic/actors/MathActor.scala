package co.com.akka.basic.actors

import akka.actor.{Actor, ActorLogging, Props}


object MathActor {

  def apply(): Props = Props(new MathActor)


  sealed trait MathOperation

  case class Sum(values: Seq[Int]) extends MathOperation

  case class Multiply(values: Seq[Int]) extends MathOperation

}


class MathActor extends Actor with ActorLogging {
  // importar todos los miembros del objeto

  import MathActor._

  val state = scala.collection.mutable.Map(
    "SUM" -> 0,
    "MULTIPLY" -> 0
  )

  def updateCounter(operation: MathOperation) = operation match {
    case Sum(_) => {
      val newValue: Int = state("SUM") + 1
      state += ("SUM" -> newValue)
    }
    case Multiply(_) => {
      val newValue: Int = state("MULTIPLY") + 1
      state += ("MULTIPLY" -> newValue)
    }
  }


  override def unhandled(message: Any): Unit = {
    log.info(s"unhandled $message")
  }

  override def receive: Receive = {
    case value @ Sum(values) => {
      val result = values.sum
      log.info(s"Sum Op: $result")
      updateCounter(value)
    }
    case value @ Multiply(values) => {
      val result = values.product
      updateCounter(value)
      log.info(s"Multiply Op: $result")
    }
  }
}
