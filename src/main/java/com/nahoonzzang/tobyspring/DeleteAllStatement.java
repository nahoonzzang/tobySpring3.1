package com.nahoonzzang.tobyspring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllStatement implements StatementStrategy {
    @Override
    public PreparedStatement makePreParedStatement(Connection connection) throws
            SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE from users");
        return preparedStatement;
    }
}
