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
						"jdbc:mysql://localhost/tobistudy",
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
			assertThat(userDao.getCount(), is(0));

			User userget1 = userDao.get(user1.getId());
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
						new ClassPathXmlApplicationContext("applicationContext.xml");

		UserDao userDao = applicationContext.getBean("userDao", UserDao.class);
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		try {
			userDao.get("unknown_id");
		} catch (ClassNotFoundException cne) {
		}
	}
}
