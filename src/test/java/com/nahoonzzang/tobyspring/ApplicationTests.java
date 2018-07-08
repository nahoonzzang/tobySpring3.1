package com.nahoonzzang.tobyspring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test // JUnit에게 테스트용 메소드임을 알림
	public void addAndGet() { // JUnit
		ApplicationContext applicationContext =
				new ClassPathXmlApplicationContext("applicationContext.xml");

		UserDao userDao = applicationContext.getBean("userDao", UserDao.class);
		User user = new User();
		user.setId("hyoju");
		user.setName("신효주");
		user.setPassword("gywnqkfkrl");

		try{
			userDao.add(user);
			assertThat(userDao.getCount(), is(1));

			User user2 = userDao.get(user.getId());

			assertThat(user2.getName(), is(user.getName()));
			assertThat(user2.getPassword(), is(user.getPassword()));
		} catch (SQLException sqlException) {
			System.out.println("실패함");
		} catch (ClassNotFoundException CNF) {
			System.out.println("실패함");
		}
	}

	@Test
	public void count() throws SQLException {
		ApplicationContext applicationContext = new GenericXmlApplicationContext(
				"applicationContext.xml");

		UserDao userDao = applicationContext.getBean("userDao", UserDao.class);
		User user1 = new User("gyumee", "박상철", "springno1");
		User user2 = new User("leegw700", "이길원", "springno2");
		User user3 = new User("bumjin", "박범진", "springno3");

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		try{
			userDao.add(user1);
			assertThat(userDao.getCount(), is(1));

			userDao.add(user2);
			assertThat(userDao.getCount(), is(2));

			userDao.add(user3);
			assertThat(userDao.getCount(), is(3));


		} catch (ClassNotFoundException CNE) {

		}


	}

}
