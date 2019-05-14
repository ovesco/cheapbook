package Model

case class Users(id : Option[Long],username : String, password: String)

case class Environnement(id : Option[Long], code : String)

case class Dependencies(id:Option[Long], dependencies: String)
