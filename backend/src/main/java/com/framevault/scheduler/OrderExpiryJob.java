package com.framevault.scheduler;

import com.framevault.usecase.CommerceUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderExpiryJob {

  private final CommerceUseCase commerce;

  public OrderExpiryJob(CommerceUseCase commerce) {
    this.commerce = commerce;
  }

  @Scheduled(fixedDelay = 60000)
  public void expire() {
    commerce.expire();
  }
}
