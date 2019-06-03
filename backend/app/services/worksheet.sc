import scala.util.Random

val x = Random.alphanumeric
val newToken = x take 32 mkString

