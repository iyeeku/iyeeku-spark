package com.iyeeku.gut.task.impl;

import com.iyeeku.gut.main.GUT;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @ClassName ColumnMetaData
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/12 18:18
 * @Version 1.0
 **/
public class ColumnMetaData {

    private int order;
    private String name;
    private String columnTypeName;
    private int columnType;
    private int precision;
    private int scale = -1;
    private int offsetA;
    private int offsetB;
    private int fixedLength;
    private int columnDisplaySize;
    private boolean isNeedReplace = true;
    private int precision_true = -1;

    public int getPrecision_true() {
        return this.precision_true;
    }

    public void setPrecision_true(int precision_true) {
        this.precision_true = precision_true;
    }

    public ColumnMetaData(){}

    public ColumnMetaData(ResultSetMetaData paramResultSetMetaData, int paramInt) throws SQLException{
        try {
            this.order = paramInt;
            this.columnTypeName = paramResultSetMetaData.getColumnTypeName(paramInt);
            this.columnType = paramResultSetMetaData.getColumnType(paramInt);
            this.name = paramResultSetMetaData.getColumnName(paramInt);
            this.scale = paramResultSetMetaData.getScale(paramInt);
            this.columnDisplaySize = paramResultSetMetaData.getColumnDisplaySize(paramInt);
            this.precision_true = paramResultSetMetaData.getPrecision(paramInt);
            this.precision = (paramResultSetMetaData.getPrecision(paramInt) == 0 ? this.columnDisplaySize : paramResultSetMetaData.getPrecision(paramInt));
            this.fixedLength = paramResultSetMetaData.getColumnDisplaySize(paramInt);
        }catch (SQLException localSQLException){
            throw localSQLException;
        }
    }

    public boolean isNeedReplace() {
        return this.isNeedReplace;
    }

    public void setNeedReplace(boolean needReplace) {
        this.isNeedReplace = needReplace;
    }

    public int getFixedLength() {
        return this.fixedLength;
    }

    public void setFixedLength(int fixedLength) {
        this.fixedLength = fixedLength;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffsetA() {
        return this.offsetA;
    }

    public void setOffsetA(int offsetA) {
        this.offsetA = offsetA;
    }

    public int getOffsetB() {
        return this.offsetB;
    }

    public void setOffsetB(int offsetB) {
        this.offsetB = offsetB;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getColumnTypeName() {
        return this.columnTypeName;
    }

    public void setColumnTypeName(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }

    public int getColumnType() {
        return this.columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }

    @Override
    public String toString() {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(getOrder());
        localStringBuffer.append("$$");
        localStringBuffer.append(getName());
        localStringBuffer.append("$$");
        localStringBuffer.append(getColumnTypeName());
        if ((!isNotNeedPrecision()) && (getPrecision() > 0)){
            localStringBuffer.append("(" + getPrecision());
            if ((getScale() > 0) || (isNeedScale())){
                localStringBuffer.append("," + getScale() + ")");
            }else{
                localStringBuffer.append(")");
            }
        }
        localStringBuffer.append("$$");
        localStringBuffer.append("(");
        localStringBuffer.append(getOffsetA() + 1 + ",");
        localStringBuffer.append(getOffsetA() + getFixedLength());
        localStringBuffer.append(")\n");
        return localStringBuffer.toString();
    }

    public boolean isNeedScale(){
        return GUT.getDbInfo().getColumnTypeNames4NeedScale().indexOf(getColumnTypeName().toUpperCase()) > -1;
    }


    public boolean isNotNeedPrecision(){
        return GUT.getDbInfo().getColumnTypeNames4NotNeedPrecision().indexOf(getColumnTypeName().toUpperCase()) > -1;
    }

    public int getColumnDisplaySize() {
        return this.columnDisplaySize;
    }

    public void setColumnDisplaySize(int columnDisplaySize) {
        this.columnDisplaySize = columnDisplaySize;
    }

}
