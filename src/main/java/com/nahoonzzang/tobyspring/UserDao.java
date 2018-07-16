package com.nahoonzzang.tobyspring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    
    private RowMapper<User> userMapper =
            (resultSet, rowNum) -> {
                User user = new User();
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                return user;
            };

    private JdbcTemplate jdbcTemplate;

    public UserDao() {}

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) throws ClassNotFoundException, SQLException {
        this.jdbcTemplate.update("INSERT INTO users(id, name, password) VALUES (?,?,?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        return this.jdbcTemplate.queryForObject(
            "SELECT * FROM users WHERE id = ?",
            new Object[] {id},
            this.userMapper);
    }

    public void deleteAll() throws SQLException {
        this.jdbcTemplate.update(
                connection -> connection.prepareStatement("DELETE FROM users"));
    }

    public int getCount() throws SQLException {
        return this.jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(
                "SELECT * FROM users ORDER BY id",
                this.userMapper);

        int a = 0;
        while (a -- > 1) {

        }
    }
}