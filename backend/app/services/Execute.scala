package services

import java.io.{File, PrintWriter}

import scala.collection.immutable.HashMap
import scala.sys.process._

object Execute {
  val ROOT_FOLDER: String = System.getProperty("user.home") + "/Documents/cheapbook"
  val BUILD_FILE: String = "scalaVersion := \"2.12.8\"\n\nname := \"hello-world\"\norganization := \"ch.epfl.scala\"\nversion := \"1.0\"\n\nlibraryDependencies += \"org.typelevel\" %% \"cats-core\" % \"1.6.0\""
  val MAIN_FILE: String = "object Main extends App {\n  println(\"Hello, World!\")\n}"

  var threads: Map[(Long, Long), Thread] = new HashMap()//[(userId, envId), thread]
  var lastResults: Map[(Long, Long), String] = new HashMap()//[(userId, envId), last result]

  def run(userId: Long, envId: Long) = {
    if (addThread(userId, envId)) startThread(userId, envId)
    threads(userId, envId).join()
    lastResults(userId, envId)
  }

  def stop(userId: Long, envId: Long) = {
    stopThread(userId, envId)
    removeThread(userId, envId)
  }

  def createProject(userId: Long, envId: Long) = {
    val path = ROOT_FOLDER + "/" + userId + "/" + envId
    val docs = new File(path)
    if (!docs.exists()) docs.mkdirs()
    if (!new File(path + "/hello").exists()) {
      new File(path + "/hello/src/main/scala/").mkdirs()
      val buildFile = new PrintWriter(new File(path + "/hello/build.sbt"))
      buildFile.write(BUILD_FILE)
      val mainFile = new PrintWriter(new File(path + "/hello/src/main/scala/Main.scala"))
      mainFile.write(MAIN_FILE)
    }

    //todo write code ?
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
      createProject(userId, envId)
      val output = cmd.!!
      lastResults += ((userId, envId) -> output)
      removeThread(userId, envId)
    })
  }

}
