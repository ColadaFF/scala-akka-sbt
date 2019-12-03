package co.com.akka.basic

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import spray.json.RootJsonFormat

import scala.concurrent.Future

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


  val route =
    concat(
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

            onComplete(saved) { _ =>
              complete("order created")
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
