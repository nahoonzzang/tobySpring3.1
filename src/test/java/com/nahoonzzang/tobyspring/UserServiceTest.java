package com.nahoonzzang.tobyspring;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {

  @Autowired
  UserService userService;
  @Autowired
  UserDao userDao;

  List<User> users; // 테스트 픽스처

  @Before
  public void setUp() {
    users = Arrays.asList(
        new User("bumjin", "박범진", "p1", Level.BASIC, 49, 0),
        new User("joytouch", "강명성", "p2", Level.BASIC, 50, 0),
        new User("erwins", "신승한", "p3", Level.SILVER, 60, 29),
        new User("madnite1", "이상호", "p4", Level.SILVER, 60, 30),
        new User("green", "오민규", "p5", Level.GOLD, 100, 100)
    );
  }

  @Test
  public void upgradeLevels() {
    userDao.deleteAll();
    for(User user : users) userDao.add(user);

    userService.upgradeLevels(); // 디비에 있는 유저 한번에 업뎃 함

    checkLevels(users.get(0), Level.BASIC);
    checkLevels(users.get(1), Level.SILVER);
    checkLevels(users.get(2), Level.SILVER);
    checkLevels(users.get(3), Level.GOLD);
    checkLevels(users.get(4), Level.GOLD);
  }


  @Test
  public void add() {
    userDao.deleteAll();

    User userWithLevel = users.get(4); // GOLD 레벨
    User userWithoutLevel = users.get(0);
    userWithoutLevel.setLevel(null);

    userService.add(userWithLevel);
    userService.add(userWithoutLevel);

    User userWithLevelRead = userDao.get(userWithLevel.getId());
    User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    assertThat(userWithLevel.getLevel(), is(userWithLevelRead.getLevel()));
    assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
  }

  private void checkLevels(User user, Level expectedLevel) {
    User userUpdate = userDao.get(user.getId());
    assertThat(userUpdate.getLevel(), is(expectedLevel));
  }


}