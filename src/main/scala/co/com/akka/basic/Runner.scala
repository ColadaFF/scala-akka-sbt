package co.com.akka.basic

import akka.actor.{ActorRef, ActorSystem, Props}
import co.com.akka.basic.actors.{MathActor, PrintActor}

import scala.concurrent.duration._
import language.postfixOps

object Runner extends App {
  val actorSystem: ActorSystem = ActorSystem("initial-system")

  val printActor: ActorRef = actorSystem.actorOf(Props[PrintActor], "print-actor")
  val mathActor: ActorRef = actorSystem.actorOf(MathActor(), "math-actor")


  printActor ! "Hello world"

  val r = scala.util.Random

  def generateRandomSeq(): Seq[Int] = {
    (1 to 10)
      .map(_ => r.nextInt(20))
  }

  import MathActor._
  import actorSystem.dispatcher

  actorSystem
    .scheduler
    .scheduleWithFixedDelay(Duration.Zero, 500 millis){() => {
      mathActor ! Sum(generateRandomSeq())
    }}


  /**
   * Exercises!
   *
   * - Agregar las clases y metodos para restart y dividir
   * - Crear una clase de petición de estado actual RequestState y hacer que cuando
   *    el actor la reciba, imprima el estado actual
   *
   * Colleciones
   * - Implementar los metodos: drop, take, filter, dropWhile, takeWhile usando solo
   *    map y reduce.
   *
   */

}
