package com.nahoonzzang.tobyspring;

import java.sql.Connection;
import java.sql.SQLException;

public class DUserDao extends UserDao {
    @Override
    public Connection getConnection() {
        return null;
    }
}
