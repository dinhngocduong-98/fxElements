package com.framevault.interactor;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.AdminAudit;
import com.framevault.repository.AdminAuditRepository;
import com.framevault.usecase.AuditUseCase;
import com.framevault.validation.ValidatorWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditInteractor implements AuditUseCase {

  private final AdminAuditRepository audits;

  public AuditInteractor(AdminAuditRepository audits, ValidatorWrapper validator) {
    this.audits = audits;
    this.validator = validator;
  }

  public void record(String actor, String action, String target) {
    validator.requireText("actor", actor);
    validator.requireText("action", action);
    validator.requireText("target", target);
    AdminAudit a = new AdminAudit();
    a.setActor(actor);
    a.setAction(action);
    a.setTarget(target);
    audits.save(a);
  }

  private final ValidatorWrapper validator;
}
