package com.nahoonzzang.tobyspring;

import java.util.ArrayList;
import java.util.List;

/**
 * 테스트 대상의 코드가 정상적으로 수행되도록 도와주기만 하는 스텁이 아니라, 부가적인 검증 기능까지 가진 목 오브젝트
 */
public class MockUserDao implements UserDao { // UserDao 구현 클래스를 대신 해야 함
  private List<User> users; // 업그레이드 후보 User 오브젝트 목록
  private List<User> updated = new ArrayList<>(); // 업그레이드 대상 오브젝트를 저장해둘 목록

  public MockUserDao(List<User> users) {
    this.users = users;
  }

  public List<User> getUpdated() {
    return this.updated;
  }

  /**
   * 스텁 기능
   *
   * @return
   */
  @Override public List<User> getAll() {
    return this.users;
  }

  /**
   * 목 오브젝트 기능
   *
   * @param user
   */
  @Override
  public void update(User user) {
    updated.add(user);
  }

  @Override public void add(User user) { throw new UnsupportedOperationException(); }
  @Override public User get(String id) { throw new UnsupportedOperationException(); }
  @Override public void deleteAll() { throw new UnsupportedOperationException(); }
  @Override public int getCount() { throw new UnsupportedOperationException(); }
}
