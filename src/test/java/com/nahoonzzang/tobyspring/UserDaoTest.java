package com.nahoonzzang.tobyspring;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserDaoTest {

	@Autowired
	UserDao userDao;

	private User user1;
	private User user2;
	private User user3;

	@Before
	public void setUp() {

		this.user1 = new User("gyumme", "박성철", "springno1", Level.BASIC, 1, 0, "0828namjin@naver.com");
		this.user2 = new User("leegw700", "이길원", "springno2", Level.BASIC, 1, 0, "0828namjin@naver.com");
		this.user3 = new User("bumjin", "박범진", "springno3", Level.BASIC, 1, 0, "0828namjin@naver.com");
	}

	@Test // JUnit에게 테스트용 메소드임을 알림
	public void addAndGet() { // JUnit

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		try{
			userDao.add(user1);
			userDao.add(user2);
			assertThat(userDao.getCount(), is(2));

			User userget1 = userDao.get(user1.getId());
			checkSameUser(userget1, user1);

			User userget2 = userDao.get(user2.getId());
			checkSameUser(userget2, user2);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@Test
	public void count() {
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		try{
			userDao.add(user1);
			assertThat(userDao.getCount(), is(1));

			userDao.add(user2);
			assertThat(userDao.getCount(), is(2));

			userDao.add(user3);
			assertThat(userDao.getCount(), is(3));
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() {
		ApplicationContext applicationContext =
				new ClassPathXmlApplicationContext("test-applicationContext.xml");

		UserDao userDao = applicationContext.getBean("userDao", UserDao.class);

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		userDao.get("unknown_id");
	}

	@Test(expected= DuplicateKeyException.class)
	public void duplicateKey() {
		userDao.deleteAll();

		userDao.add(user1);
		userDao.add(user1);
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

	@Test
	public void update() {
		userDao.deleteAll();

		userDao.add(user1);
		userDao.add(user2);

		user1.setName("오민규");
		user1.setPassword("spring6");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		userDao.update(user1);

		userDao.update(user1);

		User user1update = userDao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = userDao.get(user2.getId());
		checkSameUser(user2, user2same);
	}

	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
	}
}
