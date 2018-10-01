package com.nahoonzzang.tobyspring;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserDaoJdbc implements UserDao {

  private RowMapper<User> userMapper =
      (resultSet, rowNum) -> {
        User user = new User();
        user.setId(resultSet.getString("id"));
        user.setName(resultSet.getString("name"));
        user.setPassword(resultSet.getString("password"));
        user.setLevel(Level.valueOf(resultSet.getInt("level")));
        user.setLogin(resultSet.getInt("login"));
        user.setRecommend(resultSet.getInt("recommend"));
        user.setEmail(resultSet.getString("email"));

        System.out.println("rowNum : " + rowNum);
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

  public void add(final User user) throws DataAccessException {
    this.jdbcTemplate.update(
        "INSERT INTO users(id, name, password, Level, Login, Recommend, email) VALUES (?,?,?,?,?,?,?)",
        user.getId(), user.getName(), user.getPassword(),
        user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
  }

  public User get(String id) {
    return this.jdbcTemplate.queryForObject(
        "SELECT * FROM users WHERE id = ?",
        new Object[]{id},
        this.userMapper);
  }

  public void deleteAll() {
    this.jdbcTemplate.update(
        connection -> connection.prepareStatement("DELETE FROM users"));
  }

  public int getCount() {
    return this.jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
  }

  @Override
  public void update(User user) {
    this.jdbcTemplate.update(
        "UPDATE users set name = ?, password = ?, level = ?, login = ?, " +
            "recommend = ?, email = ? where id = ? ",
        user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(),
        user.getRecommend(), user.getEmail(), user.getId());
  }

  public List<User> getAll() {
    return this.jdbcTemplate.query(
        "SELECT * FROM users ORDER BY id",
        this.userMapper);
  }
}
