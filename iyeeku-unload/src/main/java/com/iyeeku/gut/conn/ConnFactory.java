package com.iyeeku.gut.conn;


import com.iyeeku.gut.exception.GUTException;
import com.iyeeku.gut.exception.GUTExceptionConstants;
import com.iyeeku.gut.exception.TaskException;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName ConnFactory
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/11 20:18
 * @Version 1.0
 **/
public class ConnFactory {

    private int poolSize;
    private ConnInfo connInfo;
    private List freeConnections;
    private int usedNums = 0;
    private static ConnFactory connectionFactory;

    public ConnFactory() throws GUTException{}

    public ConnFactory(ConnInfo paramConnInfo, int paramInt) throws GUTException{
        this();
        this.poolSize = paramInt;
        this.connInfo = paramConnInfo;
        this.freeConnections = new ArrayList();
        for (int i =0 ; i < this.poolSize; i++){
            this.freeConnections.add(createConnection());
        }
    }

    public static ConnFactory getConnFactory(ConnInfo paramConnInfo, int paramInt) throws GUTException{
        if (connectionFactory == null){
            connectionFactory = new ConnFactory(paramConnInfo, paramInt);
            return connectionFactory;
        }
        return connectionFactory;
    }

    public synchronized void freeConnection(Connection paramConnection){
        this.freeConnections.add(paramConnection);
        this.usedNums -= 1;
    }

    public synchronized Connection getConnection() throws GUTException{
        Connection localConnection = null;
        int i = this.freeConnections.size();
        if (i>0){
            localConnection = (Connection) this.freeConnections.get(i-1);
            this.freeConnections.remove(i-1);
            if (localConnection == null){
                localConnection = getConnection();
            }
        }
        else {
            localConnection = createConnection();
        }
        if (localConnection != null){
            this.usedNums += 1;
        }
        return localConnection;
    }

    public synchronized void release() throws GUTException{
        Iterator localIterator = this.freeConnections.iterator();
        while (localIterator.hasNext()){
            Connection localConnection = (Connection) localIterator.next();
            if (localConnection != null){
                try {
                    localConnection.close();
                }catch (SQLException localSQLException){
                    throw new TaskException(GUTExceptionConstants.GUT300100, localSQLException);
                }
            }
        }
        if (this.freeConnections != null){
            this.freeConnections.clear();
        }
    }

    private Connection createConnection() throws GUTException{
        Connection localConnection = null;
        int i = 0;
        while (true){
            i++;
            try {
                Class.forName(this.connInfo.getDriverClassName());
                localConnection = DriverManager.getConnection(this.connInfo.getUrl(), this.connInfo.getUserID(), this.connInfo.getPassword());
                if (!this.connInfo.isAutoCommit()){
                    localConnection.setAutoCommit(false);
                }
                return localConnection;
            }catch (ClassNotFoundException localClassNotFoundException){
                throw new GUTException(GUTExceptionConstants.GUT300010, new String[] { this.connInfo.getDriverClassName() }, localClassNotFoundException);
            }catch (SQLException localSQLException){
                localSQLException.printStackTrace();
                throw new GUTException(GUTExceptionConstants.GUT300020, new String[] { this.connInfo.getUrl() } , localSQLException);
            }catch (Exception localException){
                throw new GUTException(GUTExceptionConstants.GUT300000, localException);
            }
/*            catch (GUTException localGUTException){
                GUT.getLogger().info("");
            }*/
        }
    }

}
