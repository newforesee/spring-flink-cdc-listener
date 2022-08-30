package com.cummins.utils

import com.cummins.cdc.flink.configuration.FlinkConf
import com.databricks.client.jdbc.Driver
import org.springframework.stereotype.Component

import java.beans.Transient
import java.sql.{Connection, DriverManager, SQLException}
import java.util
import scala.tools.jline_embedded.internal.Log.warn


object ConnectPool {
  //静态的Connection队列
  private var connectionLinkedList: util.LinkedList[Connection] = _
  val yourToken = ""

  val yourHttpPath = ""

  val url = s"jdbc:databricks://adb-1618527118165113.1.databricks.azure.cn:443/default;TransportMode=http;SSL=1;httpPath=${yourHttpPath};AuthMech=3;UID=token;PWD=${yourToken}"
  Class.forName("com.databricks.client.jdbc.jdbc42.JDBC42AbstractDriver")

  /**
   * 获取连接,多线程访问并发控制
   *
   * @return conn
   */
  def getConnection: Connection = synchronized {
    if (connectionLinkedList == null) {
      connectionLinkedList = new util.LinkedList[Connection]
      for (i <- 0 to 8) {
        try {
          val conn: Connection = DriverManager.getConnection(url)
          connectionLinkedList.push(conn)
        } catch {
          case e: SQLException =>
            e.printStackTrace()
        }
      }
    }

    val conn: Connection = connectionLinkedList.poll
    if (conn.isValid(30)) conn else ConnectPool.createNewConnection
  }

  /**
   * 归还一个连接
   */
  def returnConnection(conn: Connection): Unit = {
    connectionLinkedList.push(conn)
  }

  def createNewConnection: Connection = {
    DriverManager.getConnection(url)
  }
}
