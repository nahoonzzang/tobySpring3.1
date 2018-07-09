package com.nahoonzzang.tobyspring;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JUnitTest {
  @Autowired
  ApplicationContext applicationContext;

  static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
  static ApplicationContext contextObject = null;

  @Test
  public void test1() {
    assertThat(testObjects, is(not(hasItem(this))));
    testObjects.add(this);

    assertThat(contextObject == null || applicationContext == this.applicationContext, is(true));
    contextObject = this.applicationContext;
  }

  @Test
  public void test2() {
    assertThat(testObjects, is(not(hasItem(this))));
    testObjects.add(this);

    assertTrue(contextObject == null || contextObject == this.applicationContext);
    contextObject = this.applicationContext;
  }

  @Test
  public void test3() {
    assertThat(testObjects, is(not(hasItem(this))));
    testObjects.add(this);

    assertThat(contextObject,
            either(is(nullValue())).or(is(this.applicationContext)));
    contextObject = this.applicationContext;
  }
}
