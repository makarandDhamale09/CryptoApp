package com.macd.crypto.service;

import com.macd.crypto.model.*;
import com.macd.crypto.utils.HttpUtils;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.args.GetArgs;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
import io.github.dengliming.redismodule.redistimeseries.DuplicatePolicy;
import io.github.dengliming.redismodule.redistimeseries.RedisTimeSeries;
import io.github.dengliming.redismodule.redistimeseries.Sample;
import io.github.dengliming.redismodule.redistimeseries.TimeSeriesOptions;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CoinsDataService {
  public static final String GET_COINS_URL =
      "https://coinranking1.p.rapidapi.com/coins?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=24h&tiers%5B0%5D=1&orderBy=marketCap&orderDirection=desc&limit=50&offset=0";
  public static final String GET_COIN_HISTORY_API = "https://coinranking1.p.rapidapi.com/coin/";
  public static final String COIN_HISTORY_TIME_PERIOD_PARAM = "/history?timePeriod=";
  public static final String REDIS_KEY_COINS = "coins";
  public static final List<String> timePeriods =
      List.of("24h", "7d", "30d", "3m", "1y", "3y", "5y");

  private final RestTemplate restTemplate;
  private final RedisJSON redisJSON;
  private final RedisTimeSeries redisTimeSeries;

  public CoinsDataService(
      RestTemplate restTemplate, RedisJSON redisJSON, RedisTimeSeries redisTimeSeries) {
    this.restTemplate = restTemplate;
    this.redisJSON = redisJSON;
    this.redisTimeSeries = redisTimeSeries;
  }

  public void fetchCoins() {
    log.info("Inside fetchCoins()");
    ResponseEntity<Coins> coinsResponseEntity =
        restTemplate.exchange(
            GET_COINS_URL, HttpMethod.GET, HttpUtils.getHttpEntity(), Coins.class);

    storeCoinsToRedisJSON(coinsResponseEntity.getBody());
  }

  public void fetchCoinsHistory() {
    log.info("Inside fetchCoinsHistory()");

    List<CoinInfo> allCoins = getAllCoinsFromRedisJson();
    allCoins.forEach(
        coinInfo -> {
          timePeriods.forEach(
              period -> {
                fetchCoinsHistoryForTimePeriod(coinInfo, period);
              });
        });
  }

  private void fetchCoinsHistoryForTimePeriod(CoinInfo coinInfo, String timePeriod) {
    log.info("Fetching Coin History of {} for Time Period {}", coinInfo.getName(), timePeriod);
    String url =
        GET_COIN_HISTORY_API + coinInfo.getUuid() + COIN_HISTORY_TIME_PERIOD_PARAM + timePeriod;
    ResponseEntity<CoinPriceHistory> coinPriceHistoryEntity =
        restTemplate.exchange(
            url, HttpMethod.GET, HttpUtils.getHttpEntity(), CoinPriceHistory.class);

    log.info(
        "Data fetched from API for Coin History of {} for Time Period {}",
        coinInfo.getName(),
        timePeriod);

    Optional.ofNullable(coinPriceHistoryEntity.getBody())
        .ifPresentOrElse(
            coinPriceHistory ->
                storeCoinHistoryToRedisTS(coinPriceHistory, coinInfo.getSymbol(), timePeriod),
            () -> {});
  }

  private void storeCoinHistoryToRedisTS(
      CoinPriceHistory coinPriceHistory, String symbol, String timePeriod) {
    log.info("Storing Coin History of {} for TimePeriod {} in Redis TS", symbol, timePeriod);

    List<CoinPriceHistoryExchangeRate> coinExchangeRate = coinPriceHistory.getData().getHistory();
    // Symbol:timePeriod
    // BTC:24h, BTC:1y, ETH:3y
    coinExchangeRate.stream()
        .filter(ch -> ch.getPrice() != null && ch.getTimestamp() != null)
        .forEach(
            ch -> {
              redisTimeSeries.add(
                  new Sample(
                      symbol + ":" + timePeriod,
                      Sample.Value.of(
                          Long.parseLong(ch.getTimestamp()), Double.parseDouble(ch.getPrice()))),
                  new TimeSeriesOptions().unCompressed().duplicatePolicy(DuplicatePolicy.LAST));
            });
    log.info(
        "Completed storing coin history of {} for time period {} into Redis TS",
        symbol,
        timePeriod);
  }

  private List<CoinInfo> getAllCoinsFromRedisJson() {
    CoinData coinData =
        redisJSON.get(
            REDIS_KEY_COINS,
            CoinData.class,
            new GetArgs().path(".data").indent("\t").newLine("\n").space(" "));
    return coinData.getCoins();
  }

  private void storeCoinsToRedisJSON(Coins coins) {
    redisJSON.set(REDIS_KEY_COINS, SetArgs.Builder.create(".", GsonUtils.toJson(coins)));
  }

  public List<CoinInfo> fetchAllCoinsFromRedisJSON() {
    return getAllCoinsFromRedisJson();
  }

  public List<Sample.Value> fetchCoinsHistoryPerTimePeriodFromRedisTs(
      String symbol, String timePeriod) {
    Map<String, Object> tsInfo = fetchTSInfoForSymbol(symbol, timePeriod);
    Long firstTimestamp = Long.valueOf(tsInfo.get("firstTimestamp").toString());
    Long lastTimestamp = Long.valueOf(tsInfo.get("lastTimestamp").toString());

    return fetchTSDataForCoin(symbol, timePeriod, firstTimestamp, lastTimestamp);
  }

  private List<Sample.Value> fetchTSDataForCoin(
      String symbol, String timePeriod, Long firstTimeStamp, Long lastTimeStamp) {
    String key = symbol + ":" + timePeriod;
    return redisTimeSeries.range(key, firstTimeStamp, lastTimeStamp);
  }

  private Map<String, Object> fetchTSInfoForSymbol(String symbol, String timePeriod) {
    return redisTimeSeries.info(symbol + ":" + timePeriod);
  }
}
