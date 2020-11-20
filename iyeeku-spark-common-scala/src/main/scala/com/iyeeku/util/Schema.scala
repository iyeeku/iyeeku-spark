package com.iyeeku.util

import java.util

import org.apache.spark.sql.types.{DataTypes, StructField, StructType}

import scala.collection.JavaConversions._

class Schema {

  def getSchema(treeMap: util.TreeMap[Integer,FileField]) : StructType = {
    val fieldList : util.List[StructField] = new util.ArrayList[StructField]
    for (entry : util.Map.Entry[Integer,FileField] <- treeMap.entrySet()){
      val fileField : FileField = entry.getValue()
      val field : StructField = DataTypes.createStructField(fileField.fieldName,DataTypes.StringType,false)
      fieldList.add(field)
    }
    return DataTypes.createStructType(fieldList)
  }



}
