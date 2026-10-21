package com.framevault.scheduler;

import com.framevault.usecase.MailUseCase;
import com.framevault.usecase.model.MailPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    name = "app.mail-worker-enabled",
    havingValue = "true",
    matchIfMissing = true)
public class MailWorker {

  private final MailUseCase mail;

  private final JavaMailSender sender;

  private final String from;

  public MailWorker(
      MailUseCase mail, JavaMailSender sender, @Value("${app.mail-from}") String from) {
    this.mail = mail;
    this.sender = sender;
    this.from = from;
  }

  @Scheduled(fixedDelay = 10000)
  public void send() {
    for (String id : mail.due()) {
      MailPayload job = mail.claim(id);
      if (job == null) continue;
      String error = null;
      try {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(from);
        m.setTo(job.getTo());
        m.setSubject(job.getSubject());
        m.setText(job.getBody());
        sender.send(m);
      } catch (Exception e) {
        error = e.getClass().getSimpleName();
      }
      mail.complete(id, job.getLease(), error);
    }
  }
}
