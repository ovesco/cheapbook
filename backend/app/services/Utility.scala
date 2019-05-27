package services

import com.google.gson.Gson

import scala.collection.immutable.HashMap

object Utility {
  val gson = new Gson
  var userTokens: Map[String, String] = new HashMap()//[token, username]

  def generateTokenForUser(username: String): Unit = userTokens += ("new token" -> username)// todo token generator
  def getUserFromToken(token: String): Option[String] = userTokens.get(token)
  def removeTokenOfUser(token: String): Unit = userTokens -= token
  def removeAllTokensOfUser(username: String): Unit = userTokens = userTokens.filter(_._2.equals(username))
}
