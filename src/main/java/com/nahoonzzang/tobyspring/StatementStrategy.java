package com.nahoonzzang.tobyspring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementStrategy {
    PreparedStatement makePreParedStatement(Connection connection) throws SQLException;
}
