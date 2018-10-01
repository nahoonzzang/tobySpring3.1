package com.nahoonzzang.tobyspring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  String id;
  String name;
  String password;
  Level level;
  int login;
  int recommend;
  String email;

  public void upgradeLevel() {
    Level nextLevel = this.level.nextLevel();
    if (nextLevel == null) {
      throw new IllegalStateException(this.level + "은 업그레이드가 불가능 합니다.");
    }
    else {
      this.level = nextLevel;
    }
  }
}
