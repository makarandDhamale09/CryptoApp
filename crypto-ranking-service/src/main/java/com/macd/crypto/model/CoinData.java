package com.macd.crypto.model;

import java.util.List;
import lombok.Data;

@Data
public class CoinData {
  public CoinStats stats;
  public List<CoinInfo> coins;
}
