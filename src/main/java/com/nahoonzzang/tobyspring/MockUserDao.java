package com.nahoonzzang.tobyspring;

import java.util.ArrayList;
import java.util.List;

public class MockUserDao implements UserDao {
  private List<User> users; // 업그레이드 후보 User 오브젝트 목록
  private List<User> updated = new ArrayList<>(); // 업그레이드 대상 오브젝트를 저장해둘 목록

  public MockUserDao(List<User> users) {
    this.users = users;
  }

  public List<User> getUpdated() {
    return this.updated;
  }

  @Override public List<User> getAll() {
    return this.users;
  }

  @Override
  public void update(User user) {
    updated.add(user);
  }

  @Override public void add(User user) { throw new UnsupportedOperationException(); }
  @Override public User get(String id) { throw new UnsupportedOperationException(); }
  @Override public void deleteAll() { throw new UnsupportedOperationException(); }
  @Override public int getCount() { throw new UnsupportedOperationException(); }
}
