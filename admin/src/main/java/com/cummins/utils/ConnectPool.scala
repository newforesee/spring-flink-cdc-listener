package com.cummins.utils

import com.databricks.client.jdbc.Driver
import org.springframework.stereotype.Component

import java.beans.Transient
import java.sql.{Connection, DriverManager, SQLException}
import java.util


object ConnectPool {
  //静态的Connection队列
  private var connectionLinkedList: util.LinkedList[Connection] = _

  val dbURL = "jdbc:databricks://adb-1618527118165113.1.databricks.azure.cn:443/default;TransportMode=http;SSL=1;httpPath=sql/protocolv1/o/1618527118165113/0622-055845-qbw7d3ae;AuthMech=3;UID=token;PWD=dapie21ede616179c88030ca46bd5abbde59"


  //此处应当使用keystore方式进行加密
  val driverName ="com.databricks.client.jdbc.jdbc42.JDBC42AbstractDriver"

  //val dbURL = "jdbc:sqlserver://dl-dev-sqlserver.database.chinacloudapi.cn:1433;database=DataIngestion"
  //jdbc:mysql://newforesee.top:3306/log_statics?useUnicode=true&characterEncoding=utf8

//  private val dbURL = "jdbc:databricks://adb-1618527118165113.1.databricks.azure.cn:443/default;TransportMode=http;SSL=1;httpPath=sql/protocolv1/o/1618527118165113/0622-055845-qbw7d3ae;AuthMech=3;UID=token;PWD=dapie21ede616179c88030ca46bd5abbde59"
  //private val dbURL: String = ApplicationProperties.DB_URL
  //private val user: String = ApplicationProperties.DB_USER
  //private val pwd: String = ApplicationProperties.DB_PWD
  try
    Class.forName(driverName)
  catch {
    case e: ClassNotFoundException =>
      e.printStackTrace()
  }

  /**
   * 获取连接,多线程访问并发控制
   *
   * @return conn
   */
  def getConnection: Connection = synchronized {
    if (connectionLinkedList == null) {
      connectionLinkedList = new util.LinkedList[Connection]
      for (i <- 0 to 5) {
        try {
          val conn: Connection = DriverManager.getConnection(dbURL)
          connectionLinkedList.push(conn)
        } catch {
          case e: SQLException =>
            e.printStackTrace()
        }
      }
    }
    connectionLinkedList.poll
  }

  /**
   * 归还一个连接
   */
  def returnConnection(conn: Connection): Unit = {
    connectionLinkedList.push(conn)
  }
}
