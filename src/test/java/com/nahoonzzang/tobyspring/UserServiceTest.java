package com.nahoonzzang.tobyspring;

import static com.nahoonzzang.tobyspring.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.nahoonzzang.tobyspring.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nahoonzzang.tobyspring.UserServiceImpl.TestUserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
  UserService userService;  // UserService 가 두개로 나눠졌더라도 ID가 UserService인 빈이 주입 될 것이다.
  @Autowired
  UserServiceImpl userServiceImpl; // 트랜잭션 롤백을 확인하기 위해 강제로 예외를 발생시킬 위치가 UserServiceImpl에 있음
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
    UserServiceImpl userServiceImpl = new UserServiceImpl(); // 고립된 테스트에서는 테스트 대상오브젝트를 직정 생성하면 됨

    // 다이내믹한 목 오브젝트 생성과 메소드의 리턴 값 설정, 그리고 DI까지 세 줄이면 충분
    UserDao mockUserDao = mock(UserDao.class);
    when(mockUserDao.getAll()).thenReturn(this.users);
    userServiceImpl.setUserDao(mockUserDao);

    // 리턴 값이 없는 메소드를 가진 목 오브젝트는 더욱 간단하게 만들 수 있다.
    MailSender mockMailSender = mock(MailSender.class);
    userServiceImpl.setMailSender(mockMailSender);

    userServiceImpl.upgradeLevels();

    // 목 오브젝트가 제공하는 검증 기능을 통해서 어떤 메소드가 몇 번 호출됐는지, 파라미터는 무엇인지 확인 할 수 있다.
    verify(mockUserDao, times(2)).update(any(User.class)); // mockUserDao의 update() 메소드가 두 번 호출됐는지 확인 하고 싶을때,
    verify(mockUserDao, times(2)).update(any(User.class));
    verify(mockUserDao).update(users.get(1)); // users.get(1)을 파라미터로 update()가 호출된 적이 있는지 확인
    assertThat(users.get(1).getLevel(), is(Level.SILVER));
    verify(mockUserDao).update(users.get(3));
    assertThat(users.get(3).getLevel(), is(Level.GOLD));

    // MailSender 의 경우는 ArgumentCaptor 라는 것을 사용해서 실제 MailSender 목 오브젝트에 전달된 파라미터를 가져와 내용을 검증하는 방법을 사용함
    // 파라미터를 직접 비교하기 보다는 파라미터 내부 정보를 확인해야 하는 경우에 유용하다.
    ArgumentCaptor<SimpleMailMessage> mailMessageArgumentCaptor =
        ArgumentCaptor.forClass(SimpleMailMessage.class); // 파라미터를 정밀하게 검사하기 위해 캡쳐할 수도 있다.
    verify(mockMailSender, times(2)).send(mailMessageArgumentCaptor.capture());
    List<SimpleMailMessage> mailMessages = mailMessageArgumentCaptor.getAllValues();
    assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
    assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));

  }

  private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
    assertThat(updated.getId(), is(expectedId));
    assertThat(updated.getLevel(), is(expectedLevel));
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