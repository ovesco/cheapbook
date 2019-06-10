package services

import com.google.gson.Gson

import scala.collection.immutable.HashMap
import scala.util.Random

object Utility {
  val gson = new Gson
  var userTokens: Map[String, Long] = new HashMap()//[token, user id]

  /**
    * generate a token for a user
    * @param userId id of the user
    * @return new token for the user
    */
  def generateTokenForUser(userId: Long): String = {
    val x = Random.alphanumeric
    var newToken = (x take 32).mkString
    while (userTokens.contains(newToken)) newToken = (x take 32).mkString
    userTokens += (newToken -> userId)
    newToken
  }

  /**
    * find user id with given token
    * @param token client's token
    * @return user id corresponding to the token
    */
  def getUserFromToken(token: String): Option[Long] = userTokens.get(token)

  /**
    * remove given token for logout
    * @param token client's token
    */
  def removeTokenOfUser(token: String): Unit = userTokens -= token
}
