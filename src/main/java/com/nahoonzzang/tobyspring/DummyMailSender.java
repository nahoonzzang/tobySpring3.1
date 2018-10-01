package com.nahoonzzang.tobyspring;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender {
  public void send(SimpleMailMessage mailMessage) throws MailException {
    System.out.println("더미메일센더");
  }

  public void send(SimpleMailMessage[] mailMessage) throws MailException {}
}
