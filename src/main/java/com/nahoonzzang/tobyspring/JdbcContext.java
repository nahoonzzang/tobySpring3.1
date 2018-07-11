package com.nahoonzzang.tobyspring;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy statementStrategy) throws
            SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.dataSource.getConnection();

            preparedStatement = statementStrategy.makePreParedStatement(connection);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (preparedStatement != null) { try { preparedStatement.close(); } catch (SQLException e) {} }
            if (connection != null) { try { connection.close(); } catch (SQLException e) {} }
        }
    }

    public void executeSql(final String query) throws SQLException {
        workWithStatementStrategy(
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreParedStatement(Connection connection) throws SQLException {
                        return connection.prepareStatement(query);
                    }
                }
        );
    }

    public void excuteSqls(final String query, String... values) throws SQLException {
        workWithStatementStrategy(
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreParedStatement(Connection connection) throws SQLException {
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        for (int i = 1; i <= values.length; i++) {
                            preparedStatement.setString(i, values[i-1]);
                        }
                        return preparedStatement;
                    }
                }
        );
    }
}
