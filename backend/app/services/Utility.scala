package services

import com.google.gson.Gson

import scala.collection.immutable.HashMap
import scala.util.Random

object Utility {
  val gson = new Gson
  var userTokens: Map[String, Long] = new HashMap()//[token, user id]

  def generateTokenForUser(userId: Long): String = {
    val x = Random.alphanumeric
    var newToken = (x take 32).mkString
    while (userTokens.contains(newToken)) newToken = (x take 32).mkString
    userTokens += (newToken -> userId)
    newToken
  }
  def getUserFromToken(token: String): Option[Long] = userTokens.get(token)
  def removeTokenOfUser(token: String): Unit = userTokens -= token
  def removeAllTokensOfUser(userId: Long): Unit = userTokens = userTokens.filter(_._2 != userId)// todo test
}
