package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from
  // main anymore.
  // Copy your code from Module#3
  // PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required
  // further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command
  // below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  // CHECKSTYLE:OFF

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo thirdparty APIs to a separate function.
  // It should be split into fto parts.
  // Part#1 - Prepare the Url to call Tiingo based on a template constant,
  // by replacing the placeholders.
  // Constant should look like
  // https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  // Where ? are replaced with something similar to <ticker> and then actual url
  // produced by
  // replacing the placeholders with actual parameters.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
    String url = buildUri(symbol, from, to);
    TiingoCandle[] responseList = restTemplate.getForObject(url, TiingoCandle[].class);
    List<Candle> candleList =new ArrayList<Candle>();
    if (responseList != null) {
      candleList = Arrays.asList(responseList);
    }
    return candleList;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "dcec30ee2bde0aef1bff3e7b63d0e539db020c7c";
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?" + "startDate=" + startDate
        + "&endDate=" + endDate + "&token=" + token;
    return uriTemplate;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
    List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();
    AnnualizedReturn annualizedReturnObj;
    Double buyPrice;
    Double sellPrice;
    try {
      for (int j = 0; j < portfolioTrades.size(); j++) {
        LocalDate startDate = portfolioTrades.get(j).getPurchaseDate();
        buyPrice = 0.0;
        sellPrice = 0.0;
        String ticker = portfolioTrades.get(j).getSymbol();
        List<Candle> candleList = getStockQuote(ticker, startDate, endDate);
        List<LocalDate> endDateList = new ArrayList<LocalDate>();
        for (int y = 0; y < candleList.size(); y++) {
          endDateList.add(candleList.get(y).getDate());
        }
        for (int i = 0; i < candleList.size(); i++) {
          if (startDate.isEqual(candleList.get(i).getDate())) {
            buyPrice = candleList.get(i).getOpen();
          }
          if (!(endDateList.contains(endDate))) {
            int k = candleList.size() - 1;
            sellPrice = candleList.get(k).getClose();
          } else {
            if (endDate.isEqual(candleList.get(i).getDate())) {
              sellPrice = candleList.get(i).getClose();
            }
          }
        }
        Double totalReturn = (sellPrice - buyPrice) / buyPrice;
        Double daysBetween = ChronoUnit.DAYS.between(startDate, endDate) * 1.0;
        Double annualizedreturns = Math.pow(1 + totalReturn, 365 / daysBetween) - 1;
        annualizedReturnObj = new AnnualizedReturn(portfolioTrades.get(j).getSymbol(), annualizedreturns, totalReturn);
        annualizedReturns.add(annualizedReturnObj);
      }
      Collections.sort(annualizedReturns, getComparator());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return annualizedReturns;
  }

}