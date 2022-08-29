package com.cummins.listener


import com.cummins.cdc.flink.sink.FlinkConsumerListener
import com.cummins.model.Order
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component

import java.util
import java.sql.{Connection, DriverManager, PreparedStatement, SQLException}
import scala.collection.JavaConverters._
import scala.tools.jline_embedded.internal.Log.warn

/**
 * Created by newforesee on 2022/8/23
 */
@Slf4j
@Component
class OrderConsumerListener extends FlinkConsumerListener[Order] {


  val yourToken = ""
  val url = s"jdbc:databricks://adb-1618527118165113.1.databricks.azure.cn:443/default;TransportMode=http;SSL=1;httpPath=sql/protocolv1/o/1618527118165113/0622-055845-qbw7d3ae;AuthMech=3;UID=token;PWD=${yourToken}"
  Class.forName("com.databricks.client.jdbc.jdbc42.JDBC42AbstractDriver")
  lazy val conn: Connection = DriverManager.getConnection(url)


  override def getDBName: String = "mydb"

  override def getTable: String = "orders"

  override def insert(data: Order): Unit = {
    conn.createStatement().execute(
      s"""
         |INSERT INTO `default`.`orders` (
         |  `order_id`,
         |	`order_date`,
         |	`customer_name`,
         |	`price`,
         |	`product_id`,
         |	`order_status`
         |)
         |VALUES
         |	(
         |    ${data.getOrder_id},
         |    '${data.getOrder_date}',
         |    '${data.getCustomer_name}',
         |    ${data.getPrice},
         |    ${data.getProduct_id},
         |    ${data.getOrder_status}
         |	)
         |""".stripMargin
    )
    println(s"Insert: $data")


  }

  override def update(srcData: Order, destData: Order): Unit = {

    conn.createStatement().execute(
      s"""
         |UPDATE `default`.`orders`
         | SET
         |  `order_id`      = ${destData.getOrder_id},
         |	`order_date`    = '${destData.getOrder_date}',
         |	`customer_name` = '${destData.getCustomer_name}',
         |	`price`         = ${destData.getPrice},
         |	`product_id`    = ${destData.getProduct_id},
         |	`order_status`  = ${destData.getOrder_status}
         | WHERE order_id   = ${srcData.getOrder_id}
         |""".stripMargin
    )

    println(s"update: \n src: $srcData \n dest: $destData")
  }

  override def delete(data: Order): Unit = {


    conn.createStatement().execute(s"DELETE FROM `default`.`orders` WHERE `order_id` = ${data.getOrder_id}")


    println(s"delete: $data")
  }

  override def batch_insert(data: util.List[Order]): Unit = {

    val ps: PreparedStatement = conn.prepareStatement(
      s"""
         |INSERT INTO `default`.`orders` (
         |  `order_id`,
         |	`order_date`,
         |	`customer_name`,
         |	`price`,
         |	`product_id`,
         |	`order_status`
         |)
         |VALUES
         |	(?,?,?,?,?,?)
         |""".stripMargin)

    data.asScala.foreach((order: Order) => {
      ps.setInt(1, order.getOrder_id)
      ps.setString(2, s"${order.getOrder_date}")
      ps.setString(3, order.getCustomer_name)
      ps.setDouble(4, order.getPrice)
      ps.setInt(5, order.getProduct_id)
      ps.setBoolean(6, order.getOrder_status)
      ps.addBatch()
    })

    ps.executeBatch()
    ps.clearBatch()


    println(s"batch_insert : ${data.size()}")

  }
}
