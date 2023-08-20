package com.macd.crypto.controller;

import com.macd.crypto.model.CoinInfo;
import com.macd.crypto.model.HistoryData;
import com.macd.crypto.service.CoinsDataService;
import com.macd.crypto.utils.Utility;
import io.github.dengliming.redismodule.redistimeseries.Sample;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/coins")
@Slf4j
public class CoinsRankingController {

  private final CoinsDataService service;

  public CoinsRankingController(CoinsDataService dataService) {
    this.service = dataService;
  }

  @GetMapping
  public ResponseEntity<List<CoinInfo>> fetchAllCoins() {
    return ResponseEntity.ok().body(service.fetchAllCoinsFromRedisJSON());
  }

  @GetMapping("/{symbol}/{timePeriod}")
  public List<HistoryData> fetchCoinHistoryPerTimePeriod(
      @PathVariable String symbol, @PathVariable String timePeriod) {

    List<Sample.Value> coinsTSData =
        service.fetchCoinsHistoryPerTimePeriodFromRedisTs(symbol, timePeriod);

    return coinsTSData.stream()
        .map(
            value ->
                new HistoryData(
                    Utility.convertUnixTimeToDate(value.getTimestamp()),
                    Utility.round(value.getValue(), 2)))
        .collect(Collectors.toList());
  }
}
