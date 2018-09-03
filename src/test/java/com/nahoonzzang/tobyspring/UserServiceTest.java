package com.nahoonzzang.tobyspring;

import static com.nahoonzzang.tobyspring.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.nahoonzzang.tobyspring.UserService.MIN_RECCOMEND_FOR_GOLD;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.nahoonzzang.tobyspring.UserService.TestUserService;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {

  @Autowired
  UserService userService;
  @Autowired
  UserDao userDao;
  @Autowired
  DataSource dataSource;
  @Autowired
  PlatformTransactionManager platformTransactionManager;

  List<User> users; // 테스트 픽스처

  @Before
  public void setUp() {
    users = Arrays.asList(
        new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
        new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
        new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
        new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
        new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
    );
  }

  @Test
  public void upgradeLevels() throws Exception {
    userDao.deleteAll();
    for(User user : users) userDao.add(user);

    userService.upgradeLevels(); // 디비에 있는 유저 한번에 업뎃 함

    // 변경된 코드
    // checkLevel(user.get(0), LEVEL.GOLD) => checkLevels(users.get(0), false);
    // : 업그레이드 될 다음 레벨을 Test코드에서 알고 있으며 괜히 번거로움. boolean 값으로 업데이트 유무만 넘겨주고
    //   checkLevels()에서 확인
    checkLevelUpgraded(users.get(0), false);
    checkLevelUpgraded(users.get(1), true);
    checkLevelUpgraded(users.get(2), false);
    checkLevelUpgraded(users.get(3), true);
    checkLevelUpgraded(users.get(4), false);
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

  // 업그레이드가 일어났나 확인
  private void checkLevelUpgraded(User user, boolean upgraded) {
    User userUpdate = userDao.get(user.getId());
    if (upgraded) {
      assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
    }
    else {
      assertThat(userUpdate.getLevel(), is(user.getLevel()));
    }
  }

  @Test
  public void upgradeAllOrNothing() throws Exception{
    UserService testUserService = new TestUserService(users.get(3).getId());
    testUserService.setUserDao(userDao); // userDao 수동 DI
    testUserService.setTransactionManager(platformTransactionManager);

    userDao.deleteAll();
    for(User user : users) userDao.add(user);

    try {
      testUserService.upgradeLevels();
      fail("TestUserServiceException expected"); // 테스트가 의도한대로 동작하는지 확인하기 위해서
    }
    catch (TestUserServiceException e) {
    }

    checkLevelUpgraded(users.get(1), false);
  }
}