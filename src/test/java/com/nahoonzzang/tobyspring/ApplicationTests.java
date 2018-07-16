package com.nahoonzzang.tobyspring;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class ApplicationTests {

	@Autowired
	UserDao userDao;

	private User user1;
	private User user2;
	private User user3;

	@Before
	public void setUp() {
		DataSource dataSource = new SingleConnectionDataSource(
						"jdbc:mysql://localhost/testdb",
						"root",
						"a10234",
						true);

		userDao.setDataSource(dataSource);
		this.user1 = new User("gyumme", "박성철", "springno1");
		this.user2 = new User("leegw700", "이길원", "springno2");
		this.user3 = new User("bumjin", "박범진", "springno3");
	}

	@Test // JUnit에게 테스트용 메소드임을 알림
	public void addAndGet() throws SQLException{ // JUnit

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		try{
			userDao.add(user1);
			userDao.add(user2);
			assertThat(userDao.getCount(), is(2));

			User userget1 = userDao.get(user1.getId());
			System.out.println("user1.getId = " + user1.getId() + "\tuser1.getName = " + user1.getName() + "\tuser1.getPassword = " + user1.getPassword() + "\n");
			System.out.println("userget1.getId = " + userget1.getId() + "\tuserget1.getName = " + userget1.getName() + "\tuserget1.getPassword = " + userget1.getPassword());
			assertThat(userget1.getId(), is(user1.getId()));
			assertThat(userget1.getPassword(), is(user1.getPassword()));

			User userget2 = userDao.get(user2.getId());
			assertThat(userget2.getId(), is(user2.getId()));
			assertThat(userget2.getPassword(), is(user2.getPassword()));
		} catch (SQLException sqlException) {
			System.out.println("실패함");
		} catch (ClassNotFoundException CNF) {
			System.out.println("실패함");
		}
	}

	@Test
	public void count() throws SQLException {
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		try{
			userDao.add(user1);
			assertThat(userDao.getCount(), is(1));

			userDao.add(user2);
			assertThat(userDao.getCount(), is(2));

			userDao.add(user3);
			assertThat(userDao.getCount(), is(3));
		} catch(ClassNotFoundException cne) {
			System.out.println(cne.getMessage());
		}
	}

	@Test(expected= EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		ApplicationContext applicationContext =
						new ClassPathXmlApplicationContext("test-applicationContext.xml");

		UserDao userDao = applicationContext.getBean("userDao", UserDao.class);
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		try {
			userDao.get("unknown_id");
		} catch (ClassNotFoundException cne) {
		}
	}

	@Test
	public void getAll() {
		try {
			userDao.deleteAll();

			List<User> users0 = userDao.getAll();
			assertThat(users0.size(), is(0));

			userDao.add(user1);
			List<User> users1 = userDao.getAll();
			assertThat(users1.size(), is(1));
			checkSameUser(user1, users1.get(0));

			userDao.add(user2);
			List<User> users2 = userDao.getAll();
			assertThat(users2.size(), is(2));
			checkSameUser(user1, users2.get(0));
			checkSameUser(user2, users2.get(1));

			userDao.add(user3);
			List<User> users3 = userDao.getAll();
			assertThat(users3.size(), is(3));
			checkSameUser(user3, users3.get(0));
			checkSameUser(user1, users3.get(1));
			checkSameUser(user2, users3.get(2));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
	}
}
