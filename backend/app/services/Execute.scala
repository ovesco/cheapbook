package services

import java.io.{File, PrintWriter}

import scala.collection.immutable.HashMap
import scala.sys.process._

object Execute {
  val ROOT_FOLDER: String = System.getProperty("user.home") + "/Documents/cheapbook"
  val BUILD_FILE_START: String = "scalaVersion := \"2.12.8\"\n\nname := \"hello-world\"\norganization := \"ch.epfl.scala\"\nversion := \"1.0\"\n\nlibraryDependencies += \"org.typelevel\" %% \"cats-core\" % \"1.6.0\"\n"
  val MAIN_FILE_START: String = "object Main extends App {\n"
  val MAIN_FILE_END: String = "\n}"

  // result kinds
  val OK = 0
  val INTERRUPT = 1
  val ERROR = 2
  val BUSY = 3

  var threads: Map[(Long, Long), Thread] = new HashMap()//[(userId, envId), thread]
  var lastResults: Map[(Long, Long), (Int, Array[String])] = new HashMap()//[(userId, envId), (kind of result, lines of result)]; kind of result: ok, interrupted, error, busy

  /**
    * run an environment
    * @param userId user id
    * @param envId environment id
    * @param env environment to run (code)
    * @param deps dependencies of the environment
    * @return tuple with kind of output (OK, ERROR, ...) and the output
    */
  def run(userId: Long, envId: Long, env: Model.Environnement, deps: Seq[Model.Dependencies]): (Int, Array[String]) = {
    if (addThread(userId, envId, env, deps)) {
      startThread(userId, envId)
      threads(userId, envId).join()
      lastResults(userId, envId)
    } else {
      (BUSY, Array("Another thread is already running"))
    }
  }

  /**
    * stop a running environment
    * @param userId user id
    * @param envId environment id
    */
  def stop(userId: Long, envId: Long) = {
    stopThread(userId, envId)
    removeThread(userId, envId)
  }

  /**
    * create a Scala project for a given environment and paste it code and dependencies
    * @param userId user id
    * @param envId environment id
    * @param env environment to run (code)
    * @param deps dependencies of the environment
    */
  def createProject(userId: Long, envId: Long, env: Model.Environnement, deps: Seq[Model.Dependencies]) = {
    val path = ROOT_FOLDER + "/" + userId + "/" + envId
    if (!new File(path + "/hello").exists()) new File(path + "/hello/src/main/scala/").mkdirs()

    val code = env.code

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

  /**
    * create a new Thread for a given environment
    * @param userId user id
    * @param envId environment id
    * @param env environment to run (code)
    * @param deps dependencies of the environment
    * @return if it's possible to create an new Thread (if no other Thread already exist for the environment)
    */
  def addThread(userId: Long, envId: Long, env: Model.Environnement, deps: Seq[Model.Dependencies]): Boolean = {
    if (threads.contains((userId, envId))) {
      false
    } else {
      val cmd = "cmd.exe /c cd " + ROOT_FOLDER + "/" + userId + "/" + envId + "/hello" + " & sbt run"
      threads += ((userId, envId) -> createNewThread(userId, envId, cmd, env, deps))
      true
    }
  }

  /**
    * start the Thread of a given environment
    * @param userId user id
    * @param envId environment id
    */
  def startThread(userId: Long, envId: Long) = {
    if (threads.contains((userId, envId))) threads((userId, envId)).start()
  }

  /**
    * interrupt the Thread of a given environment and set the result
    * @param userId user id
    * @param envId environment id
    */
  def stopThread(userId: Long, envId: Long) = {
    if (threads.contains((userId, envId))) {
      threads((userId, envId)).interrupt()
      lastResults += ((userId, envId) -> (INTERRUPT, Array("Program interrupted !")))
    }
  }

  /**
    * remove the Thread of a given environment
    * @param userId user id
    * @param envId environment id
    */
  def removeThread(userId: Long, envId: Long) = {
    threads -= ((userId, envId))
  }

  /**
    * create a new Thread to execute for a given environment
    * @param userId user id
    * @param envId environment id
    * @param cmd command the Thread must execute (cd path & sbt run)
    * @param env environment to run (code)
    * @param deps dependencies of the environment
    * @return the newly created Thread
    */
  def createNewThread(userId: Long, envId: Long, cmd: String, env: Model.Environnement, deps: Seq[Model.Dependencies]): Thread = {
    new Thread(() => {
      createProject(userId, envId, env, deps)
      try {
        val output = cmd.!!
        val result: Array[String] = output.split("\n").filter(s => !s.startsWith("[info] ") && !s.startsWith("[success] "))
        lastResults += ((userId, envId) -> (OK, result))
      } catch {
        case _: Exception => lastResults += ((userId, envId) -> (ERROR, Array("Error")))
      }
      removeThread(userId, envId)
    })
  }

}
