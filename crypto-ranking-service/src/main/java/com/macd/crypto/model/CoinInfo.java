package com.macd.crypto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class CoinInfo {
  public String uuid;
  public String symbol;
  public String name;
  public String color;
  public String iconUrl;
  public String marketCap;
  public String price;
  public int listedAt;
  public int tier;
  public String change;
  public int rank;
  public List<String> sparkline;
  public boolean lowVolume;
  public String coinrankingUrl;

  @JsonProperty("24hVolume")
  public String _24hVolume;

  public String btcPrice;
}
