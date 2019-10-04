package com.iyeeku.spark.common;

import java.io.Serializable;

public class FileField implements Serializable {

    private int fieldid;
    private String fieldname;
    private String fieldtype;
    private int fieldstart;
    private int fieldend;
    private int fieldlength;

    public FileField(){}

    public FileField(int fieldid , String fieldname , String fieldtype,
                     int fieldstart, int fieldend , int fieldlength){
        this.fieldid = fieldid;
        this.fieldname = fieldname;
        this.fieldtype = fieldtype;
        this.fieldstart = fieldstart;
        this.fieldend = fieldend;
        this.fieldlength = fieldlength;
    }

    public int getFieldid() {
        return fieldid;
    }

    public void setFieldid(int fieldid) {
        this.fieldid = fieldid;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getFieldtype() {
        return fieldtype;
    }

    public void setFieldtype(String fieldtype) {
        this.fieldtype = fieldtype;
    }

    public int getFieldstart() {
        return fieldstart;
    }

    public void setFieldstart(int fieldstart) {
        this.fieldstart = fieldstart;
    }

    public int getFieldend() {
        return fieldend;
    }

    public void setFieldend(int fieldend) {
        this.fieldend = fieldend;
    }

    public int getFieldlength() {
        return fieldlength;
    }

    public void setFieldlength(int fieldlength) {
        this.fieldlength = fieldlength;
    }

    @Override
    public String toString() {
        return "FileField [fieldid=" + fieldid + ", fieldname=" + fieldname + "" +
                ", fieldtype=" + fieldtype + ", fieldstart=" + fieldstart + "" +
                ", fieldend=" + fieldend + ", fieldlength=" + fieldlength +"]";
    }
}
