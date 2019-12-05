package co.com.akka.basic.domain
import co.com.akka.basic.domain
import org.scalatest._
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable
import scala.util.Right


class RegistrationDataSpec extends FlatSpec with Matchers {
  "Un password " should " sacar un error cuando no cumple el patrón" in {
    //arrange
    val passwordInvalid = ")((/&&"

    // act
    val validation = FormValidator.validatePassword(passwordInvalid)
    val expected: Either[DomainValidation, String] = Left(PasswordDoesNotMeetCriteria)


    // assert
    validation should be (expected)
  }

  it should "pasar para un password valido" in {
    //arrange
    val passwordValid = "password"

    // act
    val validation = FormValidator.validatePassword(passwordValid)
    val expected: Either[DomainValidation, String] = Right(passwordValid)


    // assert
    validation should be (expected)
  }

  "Un formulario de datos" should "fail for this data!" in {
    val result: domain.FormValidator.ValidationResult[RegistrationData] = FormValidator
      .validateForm(
        "username-",
        "password",
        "name",
        "lastName",
        19
      )

    println(result)
  }


  "Una pila" should "eliminar valor en LIFO" in {
    val stack = new mutable.Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be (2)
    stack.pop() should be (1)
  }

}
