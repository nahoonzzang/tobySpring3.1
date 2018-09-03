package com.nahoonzzang.tobyspring;

import java.util.List;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserService {
  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECCOMEND_FOR_GOLD = 30;

  private PlatformTransactionManager transactionManager;

  UserDao userDao;

  public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
  }

  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  public void upgradeLevels() throws Exception {
//    TransactionSynchronizationManager.initSynchronization(); // 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
    // DataSourceUtils.getConnection(); => DB커넥션 생성과 동기화를 함께 해주는 유틸리티 메소드
//    Connection connection = DataSourceUtils.getConnection(dataSource); // DB커넥션을 생성하고 트랜잭션을 시작한다.
//    connection.setAutoCommit(false);                              // 이후 DAO작업은 모두 여기서 시작한 트랜잭션 안에서 시작한다.

    // PlatformTransactionManager를 구현한 DataSourceTransactionManager 오브젝트는 JdbcTemplate에서 사용될 수 있는 방식으로 트랜잭션을 관리해준다.
    // 따라서 PlatformTransactionManager를 통해 시작한 트랜잭션은 UserDao의 JdbcTemplate 안에서 사용된다. -> 어떤 트랜잭션 매니저를 사용하는지를 UserService가 알고 있어야 하는건 DI원칙에서 어긋난다.
//    PlatformTransactionManager transactionManager =
//        new DataSourceTransactionManager(dataSource); // JDBC 트랜잭션 추상 오브젝트 생성 / 사용할 DB의 DataSource를 생성자 파라미터로 넣으면서 DataSourceTransactionManager의 오브젝트를 만든다.

    // JDBC를 이용하는 경우에는 먼저 Connection을 생성하고 나서 트랜잭션을 시작했다.
    // 하지만 PlatformTransactionManager에서는 트랜잭션을 가져오는 요청인 getTransaction() 메소드를 호출하기만 하면 된다.(트랜잭션을 가져온다는 것은 트랜잭션을 시작한다는 의미)
    // 파라미터로 넘기는 DefaultTransactionDefinition 오브젝트는 트랜잭션에 대한 속성을 담고 있다.
    // 이렇게 시작된 트랜잭션은 TransactionStatus 타입의 변수에 저장. TransactionStatus는 트랜잭션에 대한 조작이 필요하라 때 PlatformTransactionmanager메소드의 파라미터로 전달해주면 된다.
    TransactionStatus transactionStatus =
        this.transactionManager.getTransaction( // DI받은 트랜잭션 매니저를 공유해서 사용. 멀티스레드 환경에서도 안전하다.
            new DefaultTransactionDefinition()); // 트랜잭션 시작

    try {
      List<User> users = userDao.getAll();
      for (User user : users) {
        if (canUpgradeLevel(user)) {
          upgradeLevel(user);
        }
      }
      this.transactionManager.commit(transactionStatus);
    } catch (RuntimeException ex) {
      this.transactionManager.rollback(transactionStatus);
      throw ex;
    }
  }

  protected void upgradeLevel(User user) {
    user.upgradeLevel();
    userDao.update(user);
  }

  public boolean canUpgradeLevel(User user) {
    Level currentLevel = user.getLevel();
    switch(currentLevel) {
      case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
      case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
      case GOLD: return false;
      default: throw new IllegalArgumentException("Unknow Level : " + currentLevel);
    }
  }

  public void add(User user) {
    if (user.getLevel() == null) user.setLevel(Level.BASIC);
    userDao.add(user);
  }

  static class TestUserService extends UserService {
    private String id;

    TestUserService(String id) {
      this.id = id; // 예외를 발생시킬 User 오브젝트의 id를 저장할 수 있게 만든다.
    }

    protected void upgradeLevel(User user) {
      if (user.getId().equals(this.id)) throw new TestUserServiceException();
      super.upgradeLevel(user);   // 지정된 id의 User 오브젝트가 발견되면 예외를 던져서 작업을 강제로 중단시킨다
    }
  }
}
