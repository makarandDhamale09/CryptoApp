package com.macd.crypto.model;

import lombok.Data;

@Data
public class CoinStats {
  public int total;
  public int totalCoins;
  public int totalMarkets;
  public int totalExchanges;
  public String totalMarketCap;
  public String total24hVolume;
}
