package services

import java.io.{File, PrintWriter}

import scala.collection.immutable.HashMap
import scala.concurrent.Future
import scala.sys.process._

object Execute {
  val ROOT_FOLDER: String = System.getProperty("user.home") + "/Documents/cheapbook"
  val BUILD_FILE_START: String = "scalaVersion := \"2.12.8\"\n\nname := \"hello-world\"\norganization := \"ch.epfl.scala\"\nversion := \"1.0\"\n\nlibraryDependencies += \"org.typelevel\" %% \"cats-core\" % \"1.6.0\"\n"
  val MAIN_FILE_START: String = "object Main extends App {\n"
  val MAIN_FILE_END: String = "\n}"

  var threads: Map[(Long, Long), Thread] = new HashMap()//[(userId, envId), thread]
  var lastResults: Map[(Long, Long), String] = new HashMap()//[(userId, envId), last result]

  def run(userId: Long, envId: Long, code: Future[Option[Model.Environnement]], deps: Future[Seq[Model.Dependencies]]) = {
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
    if (!new File(path + "/hello").exists()) new File(path + "/hello/src/main/scala/").mkdirs()

    //todo write code and dependencies
    val code = "println(\"Hello World\")"
    val deps = List("", "")

    val buildFile = new PrintWriter(new File(path + "/hello/build.sbt"))
    buildFile.write(BUILD_FILE_START)
    deps.foreach(d => buildFile.write(d + "\n"))
    buildFile.flush()
    buildFile.close()
    val mainFile = new PrintWriter(new File(path + "/hello/src/main/scala/Main.scala"))
    mainFile.write(MAIN_FILE_START)
    mainFile.write(code)
    mainFile.write(MAIN_FILE_END)
    mainFile.flush()
    mainFile.close()
  }

  def addThread(userId: Long, envId: Long): Boolean = {
    if (threads.contains((userId, envId))) {
      false
    } else {
      val cmd = "cmd.exe /c cd " + ROOT_FOLDER + "/" + userId + "/" + envId + "/hello" + " & sbt run"
      threads += ((userId, envId) -> createNewThread(userId, envId, cmd))// todo
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
      try {
        val output = cmd.!!
        val result = output.split("\n").filter(s => !s.startsWith("[info] ") && !s.startsWith("[success] ")).mkString("\n")
        lastResults += ((userId, envId) -> result)
      } catch {
        case _: Exception => lastResults += ((userId, envId) -> "Error !")
      }
      removeThread(userId, envId)
    })
  }

}
