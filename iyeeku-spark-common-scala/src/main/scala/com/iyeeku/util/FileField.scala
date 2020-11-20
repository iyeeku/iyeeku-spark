package com.iyeeku.util

class FileField (
  var fieldId : Int,
  var fieldName : String,
  var fieldType : String,
  var fieldStart : Int,
  var fieldEnd : Int,
  var fieldLength : Int) extends Serializable {

  def this() = {
    this(10,"name","String",1,10,10)
  }

  def setFieldId(fieldId : Int)  = {
    this.fieldId = fieldId
  }


}
