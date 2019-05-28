package services

import com.google.gson.Gson

import scala.collection.immutable.HashMap

object Utility {
  val gson = new Gson
  var userTokens: Map[String, String] = new HashMap()//[token, username]

  def generateTokenForUser(username: String): String = {
    val newToken = "new token"// todo token generator
    userTokens += (newToken -> username)
    newToken
  }
  def getUserFromToken(token: String): Option[String] = userTokens.get(token)
  def removeTokenOfUser(token: String): Unit = userTokens -= token
  def removeAllTokensOfUser(username: String): Unit = userTokens = userTokens.filter(_._2.equals(username))
}
