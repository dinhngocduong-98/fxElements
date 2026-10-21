package com.framevault.interactor;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.EmailAttempt;
import com.framevault.domain.model.EmailJob;
import com.framevault.repository.EmailAttemptRepository;
import com.framevault.repository.EmailJobRepository;
import com.framevault.repository.page.PageRequest;
import com.framevault.shared.Failures;
import com.framevault.usecase.MailUseCase;
import com.framevault.validation.ValidatorWrapper;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MailInteractor implements MailUseCase {

  private final EmailJobRepository jobs;

  private final EmailAttemptRepository attempts;

  public MailInteractor(
      EmailJobRepository jobs, EmailAttemptRepository attempts, ValidatorWrapper validator) {
    this.jobs = jobs;
    this.attempts = attempts;
    this.validator = validator;
  }

  public String enqueue(String to, String subject, String body) {
    validator.requireText("recipient", to);
    validator.requireText("subject", subject);
    validator.requireText("body", body);
    EmailJob j = new EmailJob();
    j.setId(UUID.randomUUID().toString());
    j.setRecipient(to);
    j.setSubject(subject);
    j.setBody(body);
    j.setStatus("PENDING");
    j.setNextAttempt(Instant.now());
    jobs.save(j);
    return j.getId();
  }

  @Transactional(readOnly = true)
  public List<String> due() {
    return jobs.due(Instant.now(), PageRequest.of(0, 10)).stream()
        .map(j -> j.getId())
        .collect(Collectors.toList());
  }

  public com.framevault.usecase.model.MailPayload claim(String id) {
    validator.requireText("id", id);
    EmailJob j = jobs.lockById(id).orElse(null);
    Instant now = Instant.now();
    if (j == null) return null;
    boolean ready =
        (Set.of("PENDING", "RETRY_WAIT").contains(j.getStatus())
                && !j.getNextAttempt().isAfter(now))
            || ("PROCESSING".equals(j.getStatus())
                && j.getLeaseUntil() != null
                && j.getLeaseUntil().isBefore(now));
    if (!ready) return null;
    j.setStatus("PROCESSING");
    j.setAttempts(j.getAttempts() + 1);
    j.setLeaseUntil(now.plusSeconds(600).truncatedTo(java.time.temporal.ChronoUnit.SECONDS));
    jobs.save(j);
    return new com.framevault.usecase.model.MailPayload(
        j.getId(),
        j.getRecipient(),
        j.getSubject(),
        j.getBody(),
        j.getAttempts(),
        j.getLeaseUntil());
  }

  public void complete(String id, Instant lease, String error) {
    validator.requireText("id", id);
    EmailJob j = jobs.lockById(id).orElseThrow(Failures::missing);
    if (!"PROCESSING".equals(j.getStatus()) || !Objects.equals(j.getLeaseUntil(), lease)) return;
    EmailAttempt a = new EmailAttempt();
    a.setJobId(id);
    a.setSuccess(error == null);
    a.setErrorCode(error);
    attempts.save(a);
    if (error == null) {
      j.setStatus("SENT");
      j.setBody("[redacted after delivery]");
      j.setLastError(null);
    } else {
      long[] delay = {60, 300, 1800, 7200, 21600};
      j.setStatus(j.getAttempts() >= 5 ? "DEAD" : "RETRY_WAIT");
      j.setNextAttempt(Instant.now().plusSeconds(delay[Math.min(j.getAttempts() - 1, 4)]));
      j.setLastError(error);
    }
    jobs.save(j);
  }

  private final ValidatorWrapper validator;
}
