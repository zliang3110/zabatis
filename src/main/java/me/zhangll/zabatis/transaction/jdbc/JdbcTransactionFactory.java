package me.zhangll.zabatis.transaction.jdbc;

import me.zhangll.zabatis.session.TransactionIsolationLevel;
import me.zhangll.zabatis.transaction.Transaction;
import me.zhangll.zabatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

public class JdbcTransactionFactory implements TransactionFactory {

    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
