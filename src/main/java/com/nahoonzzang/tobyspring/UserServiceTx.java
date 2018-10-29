package com.nahoonzzang.tobyspring;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * UserService 의 부가 기능 클래스
 */
public class UserServiceTx implements UserService {
  UserService userService;
  PlatformTransactionManager transactionManager;

  public void setTransactionManager(
      PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void add(User user) {
    userService.add(user);
  }

  @Override
  public void upgradeLevels() {
    TransactionStatus status = this.transactionManager
        .getTransaction(new DefaultTransactionDefinition());

    try {
      userService.upgradeLevels();
      this.transactionManager.commit(status);
    } catch (RuntimeException ex) {
      this.transactionManager.rollback(status);
      throw ex;
    }
  }
}
