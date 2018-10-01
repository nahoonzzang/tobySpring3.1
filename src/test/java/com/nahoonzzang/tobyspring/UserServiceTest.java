package com.nahoonzzang.tobyspring;

import static com.nahoonzzang.tobyspring.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.nahoonzzang.tobyspring.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.nahoonzzang.tobyspring.UserServiceImpl.TestUserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {

  @Autowired
  UserService userService;
  @Autowired
  UserServiceImpl userServiceImpl;
  @Autowired
  UserDao userDao;
  @Autowired
  DataSource dataSource;
  @Autowired
  PlatformTransactionManager platformTransactionManager;
  @Autowired
  MailSender mailSender;

  List<User> users; // 테스트 픽스처

  @Before
  public void setUp() {
    users = Arrays.asList(
        new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "0828namjin@naver.com"),
        new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "0828namjin@naver.com"),
        new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "0828namjin@naver.com"),
        new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "0828namjin@naver.com"),
        new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "0828namjin@naver.com")
    );
  }

  @Test
  @DirtiesContext // 컨텍스트의 DI 설정을 변경하는 테스트라는 것을 알려준다. mailSender 수동 DI
  public void upgradeLevels() throws Exception {
    System.out.println("테스트 시작");
    userDao.deleteAll();
    int i = 0;
    for(User user : users) {
      i++;
      System.out.println(i + "번째 user_" + "이름 : " + user.getName() + " 이메일 : " + user.getEmail());
      userDao.add(user);
    }

    MockMailSender mockMailSender = new MockMailSender();
    userServiceImpl.setMailSender(mockMailSender);

    userService.upgradeLevels(); // 디비에 있는 유저 한번에 업뎃

    checkLevelUpgraded(users.get(0), false);
    checkLevelUpgraded(users.get(1), true);
    checkLevelUpgraded(users.get(2), false);
    checkLevelUpgraded(users.get(3), true);
    checkLevelUpgraded(users.get(4), false);


    // mock object 에 저장된 메일 수신자 목록을 가져와 업그레이드 대상과 일치하는지 확인한다.
    List<String> request = mockMailSender.getRequests();
    assertThat(request.size(), is(2));
    assertThat(request.get(0), is(users.get(1).getEmail()));
    assertThat(request.get(1), is(users.get(3).getEmail()));
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
  public void upgradeAllOrNothing() {
    TestUserService testUserService = new TestUserService(users.get(3).getId());
    testUserService.setUserDao(userDao); // userDao 수동 DI
    testUserService.setMailSender(mailSender);

    UserServiceTx txUserService = new UserServiceTx();
    txUserService.setTransactionManager(platformTransactionManager);
    txUserService.setUserService(testUserService);

    userDao.deleteAll();
    for(User user : users) userDao.add(user);

    try {
      txUserService.upgradeLevels();
      fail("TestUserServiceException expected"); // 테스트가 의도한대로 동작하는지 확인하기 위해서
    }
    catch (TestUserServiceException e) {

    }

    checkLevelUpgraded(users.get(1), false);
  }

  static class MockMailSender implements MailSender {
    private List<String> requests = new ArrayList<String>(); // UserService로부터 전송 요청을 받은 메일 주소를 저장해 두고 이를 읽을 수 있게 한다.

    public List<String> getRequests() {
      return requests;
    }

    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
      System.out.println("\n\n호출됨\n\n");
      requests.add(simpleMailMessage
          .getTo()[0]); // 전송 요청을 받은 이메일 주소를 저장해둔다. 간다하게 첫 번째 수신자 메일 주소만 저장 (한번에 한명씩 보내기 때문에 하나뿐일 것.)
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {}
  }
}