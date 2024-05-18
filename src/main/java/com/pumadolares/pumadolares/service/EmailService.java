package com.pumadolares.pumadolares.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private SpringTemplateEngine templateEngine;

  public void sendEmail(String to, String subject, String message) throws MessagingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

    helper.setTo(to);
    helper.setSubject(subject);

    Context context = new Context();
    context.setVariable("subject", subject);
    context.setVariable("message", message);

    String htmlContent = templateEngine.process("api/user/notify", context);
    helper.setText(htmlContent, true);

    mailSender.send(mimeMessage);

  }
}
