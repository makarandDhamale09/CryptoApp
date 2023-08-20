package com.macd.crypto;

import com.macd.crypto.service.CoinsDataService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

  private final CoinsDataService service;

  public ApplicationStartup(CoinsDataService service) {
    this.service = service;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    // This will add the data from rapid API to redis on app startup
    // service.fetchCoins();
    // service.fetchCoinsHistory();
  }
}
