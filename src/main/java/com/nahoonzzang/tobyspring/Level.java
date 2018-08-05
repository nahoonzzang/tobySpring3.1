package com.nahoonzzang.tobyspring;

public enum Level {
  GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);

  // 지정된 값을 저장할 수 있는 인스턴스 변수와 생성자를 새로 추가해 주어야 한다.
  // ※ 주의할 점은, 먼저 열거형 상수를 모두 정의한 다음에 다른 멤버들을 추가해야한다는 것.
  private final int value;
  private final Level next;

  // 열거형의 생성자는 생성 불가, 열거형의 생성자는 묵시적으로 private 이기 때문에
  Level(int value, Level next) {
    this.value = value;
    this.next = next;
  }

  public int intValue() {
    return value;
  }

  public Level nextLevel() { return this.next; }

  public static Level valueOf(int value) {
    switch(value) {
      case 1: return BASIC;
      case 2: return SILVER;
      case 3: return GOLD;
      default: throw new AssertionError("Unknown value: " + value);
    }
  }
}
