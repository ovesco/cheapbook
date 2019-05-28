package services

import com.google.gson.Gson

import scala.collection.immutable.HashMap
import scala.util.Random

object Utility {
  val gson = new Gson
  var userTokens: Map[String, String] = new HashMap()//[token, username]

  def generateTokenForUser(username: String): String = {
    val x = Random.alphanumeric
    val newToken = (x take 32).mkString
    userTokens += (newToken -> username)
    newToken
  }
  def getUserFromToken(token: String): Option[String] = userTokens.get(token)
  def removeTokenOfUser(token: String): Unit = userTokens -= token
  def removeAllTokensOfUser(username: String): Unit = userTokens = userTokens.filter(_._2.equals(username))
}
