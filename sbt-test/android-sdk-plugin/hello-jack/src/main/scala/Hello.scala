package hello

import argonaut._, Argonaut._

object Hello {
  import Person._

  def encodePerson(person: Person) = person.asJson.spaces4
}
