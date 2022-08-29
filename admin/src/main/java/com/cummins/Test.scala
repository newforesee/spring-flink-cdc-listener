package com.cummins

import com.databricks.client.jdbc.Driver


import java.sql.Connection
import java.util
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * Created by newforesee on 2022/8/23
 */
object Test {
  def main(args: Array[String]): Unit = {
  /*  val jdbcUrl = "jdbc:databricks://adb-1618527118165113.1.databricks.azure.cn:443/default;TransportMode=http;SSL=1;httpPath=sql/protocolv1/o/1618527118165113/0622-055845-qbw7d3ae;AuthMech=3;UID=token;PWD=dapie21ede616179c88030ca46bd5abbde59"
    val ds = new SimpleDriverDataSource()

    ds.setDriver(new Driver())
    ds.setUrl(jdbcUrl)

    val template = new JdbcTemplate(ds)

    val resultList: List[mutable.Map[String, AnyRef]] = template.queryForList("select * from od_mo.highsulpher_5655_model_output limit 10").asScala.toList.map(_.asScala)



    resultList.foreach(x=>{
      x.foreach((m: (String, AnyRef)) =>{
        print(s"${m._1} : ${m._2.toString}")
      })
      println("|")
      println("---------------------------------------------------")
    })*/



    


  }

}
