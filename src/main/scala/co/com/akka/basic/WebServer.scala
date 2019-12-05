package co.com.akka.basic

import java.time.LocalTime

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Source
import co.com.akka.basic.repositories.UsersRepository
import spray.json.RootJsonFormat

import scala.concurrent.Future
import scala.util.{Failure, Success}

// For json
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object WebServer extends App with DbConfig {
  import DbConfig._

  implicit val system: ActorSystem = ActorSystem("actor-system-http")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher


  setupDb(db) // Ejecución de planos

  val usersRepository = new UsersRepository(db, usersTableQuery)

  // domain objects
  final case class Item(name: String, id: Long)

  final case class Order(items: List[Item])

  // format for unmarshalling and marshalling
  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat2(Item)
  implicit val orderFormat: RootJsonFormat[Order] = jsonFormat1(Order)

  var orders: List[Item] = Nil


  def fetchItem(itemId: Long): Future[Option[Item]] = Future {
    orders.find(order => order.id == itemId)
  }

  def saveOrder(order: Order): Future[Done] = {
    orders = order match {
      case Order(items) => items ::: orders
      case _ => orders
    }
    Future {
      Done
    }
  }


  /**
   * Homework
   *
   * Crear una api REST con:
   *
   * 1. CRUD de usuario a base de datos
   * 2. Con prueba unitarias
   * 3. Con pruebas de integración usando ScalaMock
   * 4. Con validaciones usando Cats Validated
   * 5. Extra: ¿Cómo usar el tipo Reader de Cats para las operaciones en base de datos?
   * 6. Extra: Que todas las operaciones de base de datos pasen por un actor,
   *  investigar patron ask de Akka
   * 7. Crear un endpoint HTTP de Server Sent Events para reportar todos los eventos de CRUD
   *
   */


  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
  import scala.concurrent.duration._
  import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
  val route =
    concat(

      path("events") {
        get {
          complete {
            Source
              .tick(2.seconds, 2.seconds, NotUsed)
              .map(_ => LocalTime.now())
              .map(time => ServerSentEvent(ISO_LOCAL_TIME.format(time)))
              .keepAlive(1.second, () => ServerSentEvent.heartbeat)
          }
        }
      },
      path("users") {
        concat(
          post {
            complete("")
          },
          get {
            concat(
              path("one") {
                complete("")
              },
              path("all") {
                complete("")
              }
            )
          }
        )
      },
      get {
        pathPrefix("item" / LongNumber) {id =>
          val maybeItem: Future[Option[Item]] = fetchItem(id)


          onSuccess(maybeItem) {
            case Some(value) => complete(value)
            case None => complete(StatusCodes.NotFound)
          }
        }
      },
      post {
        path("create-order") {
          entity(as[Order]) { order =>
            val saved:Future[Done] = saveOrder(order)

            onComplete(saved) {
              case Success(value) => complete("")
              case Failure(exception) => complete("Fallo")
            }

          }
        }
      }



      /*path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hello World</h1>"))
        }
      },
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Index"))
      }*/
    )

  // intentar levantar el servidor
  val bindingFuture = Http()
    .bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress return to Stop.")
  StdIn.readLine() // lo deja correr hasta que el usuario presione una tecla

  bindingFuture
    .flatMap(server => server.unbind()) // limpiar la conexion con el puerto 8080
    .onComplete(_ => system.terminate()) // y apagar el sistema de actores cuando haya terminado


}
