package hello

import argonaut._, Argonaut._

case class Person(name: String, age: Int, things: List[String])

object Person {
  implicit def jsonCodec =
    casecodec3(Person.apply, Person.unapply)("name", "age", "things")
}