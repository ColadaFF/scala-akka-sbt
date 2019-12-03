val list = Seq("a", "b", "c")


def drop[T](values: Seq[T], number: Int): Seq[T] = {
  val counterRange = 0 to list.length


  case class Pair(index: Int, value: T)
  counterRange
    .map(index => Pair(index, values(index)))
    .foldLeft(Seq[T]())((acc, actual) => {
      if(actual.index < number) {
        acc
      } else {
        acc.+:(actual.value)
      }
    })
}



def notA(value: String): Boolean =  value != "a"



implicit class SeqExtension[T](targetList: Seq[T]) {
  def dropWhileCustom(predicate: T => Boolean): Seq[T] = {

    val initialValue = Seq[T]()
    targetList
      .foldLeft(initialValue)((acc, actual) => {
        if(predicate(actual)) {
          acc
        } else {
          acc.+:(actual)
        }
      })
  }
}


list.dropWhileCustom(notA)



def multiply(num: Int, times: Int): Int = num * times

multiply(1, 3)

// higher order functions

def multiplyCurry(times: Int)(num: Int): Int = num * times

def times3: Int => Int = multiplyCurry(3)


times3(3) // 9
times3(4) // 12



def multiplyImplicit(num: Int)(implicit times: Int) = num * times


implicit val times:Int = 3
multiplyImplicit(4) // 12



val tuple: (Int, String, String) = (1, "name", "lastName")


// ADT - Sum Type
// tipo superior -> pueden ser uno u otro
// Functor  => Tipo que tenga el metodo map => Type[T] => T => B
// Monad => FlatMap
/**
 * Try[T] => Failure | Success
 * Option[T] => Some(T) | None
 * Either[T] => Left | Right
 */





