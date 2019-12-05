package co.com.akka.basic.domain

import cats.data._
import cats.data.Validated._
import cats.implicits._

final case class RegistrationData(
                                   username: String,
                                   password: String,
                                   firstName: String,
                                   lastName: String,
                                   age: Int
                                 )

sealed trait DomainValidation {
  def errorMessage: String
}

case object UsernameHasSpecialCharacters extends DomainValidation {
  override def errorMessage: String = "El usuario no puede tener caracteres especiales"
}

case object PasswordDoesNotMeetCriteria extends DomainValidation {
  override def errorMessage: String = "Password error"
}

case object FirstNameHasSpecialCharacters extends DomainValidation {
  override def errorMessage: String = "El nombre no puede tener caracteres especiales"
}

case object LastNameHasSpecialCharacters extends DomainValidation {
  override def errorMessage: String = "El apellido no puede tener caracteres especiales"
}

case object AgeDoesNotMeetCriteria extends DomainValidation {
  override def errorMessage: String = "La edad no es apropiada"
}


sealed trait FormValidator {

  type ValidationResult[A] = ValidatedNec[DomainValidation, A]

  def validateUserName(username: String): ValidationResult[String] =
    if (username.matches("^[a-zA-Z0-9]+$"))
      username.validNec
    else UsernameHasSpecialCharacters.invalidNec


  def validatePassword(value: String): ValidationResult[String] =
    if (value.matches("^[a-zA-Z0-9]+$"))
      value.validNec
    else PasswordDoesNotMeetCriteria.invalidNec

  def validateFirstName(value: String): ValidationResult[String] =
    if (value.matches("^[a-zA-Z0-9]+$"))
      value.validNec
    else FirstNameHasSpecialCharacters.invalidNec

  def validateLastName(value: String): ValidationResult[String] =
    if (value.matches("^[a-zA-Z0-9]+$"))
      value.validNec
    else LastNameHasSpecialCharacters.invalidNec

  def validateAge(value: Int): ValidationResult[Int] =
    if (value >= 18 && value <= 75)
      value.validNec
    else AgeDoesNotMeetCriteria.invalidNec

  /**
   *
   * Either => Monad => Flatmap
   * Validated => Applicative Functor
   *
   * NEC => Not Empty Chain = [1] por lo menos un valor
   *
   *
   * Validations
   */

  def validateForm(username: String,
                   password: String,
                   firstName: String,
                   lastName: String,
                   age: Int): ValidationResult[RegistrationData] = {
    (
      validateUserName(username),
      validatePassword(password),
      validateFirstName(firstName),
      validateLastName(lastName),
      validateAge(age)
    ).mapN(RegistrationData)

  }
}

object FormValidator extends FormValidator
