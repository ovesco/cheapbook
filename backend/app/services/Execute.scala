package services

import scala.collection.immutable.HashMap
import scala.sys.process._

object Execute {
  var threads: Map[(Long, Long), Thread] = new HashMap()//[(userId, envId), thread]
  var lastResults: Map[(Long, Long), String] = new HashMap()//[(userId, envId), last result]

  def run(userId: Long, envId: Long) = {
    if (addThread(userId, envId)) startThread(userId, envId)
    // todo wait result then remove thread
  }

  def stop(userId: Long, envId: Long) = {
    stopThread(userId, envId)
    removeThread(userId, envId)
  }

  def addThread(userId: Long, envId: Long): Boolean = {
    if (threads.contains((userId, envId))) {
      false
    } else {
      threads += ((userId, envId) -> createNewThread(userId, envId, "cmd.exe dir"))// todo
      true
    }
  }

  def startThread(userId: Long, envId: Long) = {
    if (threads.contains((userId, envId))) threads((userId, envId)).start()
  }

  def stopThread(userId: Long, envId: Long) = {
    if (threads.contains((userId, envId))) threads((userId, envId)).interrupt()
  }

  def removeThread(userId: Long, envId: Long) = {
    threads -= ((userId, envId))
  }

  def createNewThread(userId: Long, envId: Long, cmd: String): Thread = {
    new Thread(() => {
      val output = cmd.!!
      lastResults += ((userId, envId) -> output)
    })
  }

}
