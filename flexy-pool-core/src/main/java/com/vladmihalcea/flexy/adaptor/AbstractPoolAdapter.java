package com.vladmihalcea.flexy.adaptor;

import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.connection.Credentials;
import com.vladmihalcea.flexy.context.Context;
import com.vladmihalcea.flexy.metric.Timer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * AbstractPoolAdapter - Abstract class for PoolAdapter instances.
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractPoolAdapter<T extends DataSource> implements PoolAdapter {

    private final Context context;

    private final T dataSource;

    private final Timer connectionAcquireTimer;

    public AbstractPoolAdapter(Context context, T dataSource) {
        this.context = context;
        this.dataSource = dataSource;
        this.connectionAcquireTimer = context.getMetrics().timer("connectionAcquireTimer");
    }

    @Override
    public T getDataSource() {
        return dataSource;
    }

    @Override
    public Connection getConnection(ConnectionRequestContext context) throws SQLException {
        try {
            Credentials credentials = context.getCredentials();
            return (credentials == null) ?
                    dataSource.getConnection() :
                    dataSource.getConnection(credentials.getUsername(), credentials.getPassword());
        } catch (SQLException e) {
            throw launderSQLException(e);
        } catch (RuntimeException e) {
            throw launderRuntimeException(e);
        }
    }

    protected SQLException launderSQLException(SQLException e) {
        return e;
    }

    protected RuntimeException launderRuntimeException(RuntimeException e) {
        return e;
    }
}
