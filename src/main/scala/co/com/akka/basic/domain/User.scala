package co.com.akka.basic.domain



case class CreateUserRequest(name: String, lastName: String)
case class User(id: Int, name: String, lastName: String)
