package me.zhangll.zabatis.transaction.jdbc;

import me.zhangll.zabatis.logging.Log;
import me.zhangll.zabatis.logging.LogFactory;
import me.zhangll.zabatis.session.TransactionIsolationLevel;
import me.zhangll.zabatis.transaction.Transaction;
import me.zhangll.zabatis.transaction.TransactionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransaction implements Transaction {
    private static final Log log = LogFactory.getLog(JdbcTransaction.class);

    protected Connection connection;
    protected DataSource dataSource;
    protected TransactionIsolationLevel level;
    protected boolean autoCommit;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null){
            openConnection();
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            if (log.isDebugEnabled()) {
                log.debug("Committing JDBC Connection [" + connection + "]");
            }
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && connection.getAutoCommit()){
            if (log.isDebugEnabled()){
                log.debug("回滚JDBC连接："+ connection);
            }
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null){
            resetAutoCommit();
            if (log.isDebugEnabled()){
                log.debug("关闭JDBC连接："+connection);
            }
            connection.close();
        }
    }

    protected void setDesiredAutoCommit(boolean desiredAutoCommit){
        try {
            if (connection.getAutoCommit() != desiredAutoCommit){
                if (log.isDebugEnabled()){
                    log.debug("Setting autocommit to " + desiredAutoCommit + " on JDBC Connection [" + connection + "]");
                }
                connection.setAutoCommit(desiredAutoCommit);
            }
        } catch (SQLException e) {
            throw new TransactionException("Error configuring AutoCommit.  "
                    + "Your driver may not support getAutoCommit() or setAutoCommit(). "
                    + "Requested setting: " + desiredAutoCommit + ".  Cause: " + e, e);
        }
    }

    protected void resetAutoCommit(){
        try {
            if (!connection.getAutoCommit()){
                if (log.isDebugEnabled()){
                    log.debug("设置JDBC Connection为自动提交： [" + connection + "]");
                }
                connection.setAutoCommit(true);
            }
        }catch (SQLException e){
            if (log.isDebugEnabled()) {
                log.debug("Error resetting autocommit to true "
                        + "before closing the connection.  Cause: " + e);
            }
        }
    }

    protected void openConnection() throws SQLException{
        if (log.isDebugEnabled()){
            log.debug("打开JDBC Connection");
        }

        connection = dataSource.getConnection();

        if (level != null){
            connection.setTransactionIsolation(level.getLevel());
        }

        setDesiredAutoCommit(autoCommit);
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}
