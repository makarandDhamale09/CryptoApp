package com.macd.crypto.model;

import java.util.List;
import lombok.Data;

@Data
public class CoinPriceHistoryData {
  public String change;
  public List<CoinPriceHistoryExchangeRate> history;
}
