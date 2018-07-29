package com.nahoonzzang.tobyspring;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class UserDaoJdbc implements UserDao{
    private RowMapper<User> userMapper =
            (resultSet, rowNum) -> {
                User user = new User();
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setLevel(Level.valueOf(resultSet.getInt("level")));
                user.setLogin(resultSet.getInt("login"));
                user.setRecommend(resultSet.getInt("recommend"));

                return user;
            };

    private JdbcTemplate jdbcTemplate;

    public UserDaoJdbc() {}

    public UserDaoJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) throws DataAccessException{
        this.jdbcTemplate.update(
            "INSERT INTO users(id, name, password, Level, Login, Recommend) VALUES (?,?,?,?,?,?)",
                user.getId(), user.getName(), user.getPassword(),
            user.getLevel().intValue(), user.getLogin(), user.getRecommend());

    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                new Object[] {id},
                this.userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update(
                connection -> connection.prepareStatement("DELETE FROM users"));
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(
                "SELECT * FROM users ORDER BY id",
                this.userMapper);

    }
}
