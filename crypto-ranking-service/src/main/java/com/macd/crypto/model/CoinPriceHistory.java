package com.macd.crypto.model;

import lombok.Data;

@Data
public class CoinPriceHistory {
  public String status;
  public CoinPriceHistoryData data;
}
