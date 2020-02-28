package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Read the json file provided in the argument[0]. The file will be avilable in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. parse the json file using ObjectMapper provided with #getObjectMapper,
  // and extract symbols provided in every trade.
  // return the list of all symbols in the same order as provided in json.
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json"
  // Make sure that it prints below String on the console -
  // ["AAPL","MSFT","GOOGL"]
  // Now, run
  // ./gradlew build and make sure that the build passes successfully
  // There can be few unused imports, you will need to fix them to make the build
  // pass.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    List<PortfolioTrade> portfolioDataList = portfolioTrade(args);
    List<String> symbolList = new ArrayList<String>();
    for (int i = 0; i < portfolioDataList.size(); i++) {
      if (portfolioDataList.get(i) != null) {
        symbolList.add(portfolioDataList.get(i).getSymbol());
      }
    }
    return symbolList;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename)
    .toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/r-keerthireddy74-"
        + "ME_QMONEY/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = getObjectMapper().toString();
    String functionNameFromTestFileInStackTrace = "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "22";

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, 
      toStringOfObjectMapper,functionNameFromTestFileInStackTrace, 
      lineNumberFromTestFileInStackTrace });
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Copy the relevant code from #mainReadQuotes to parse the Json into PortfolioTrade list and
  //  Get the latest quotes from TIingo.
  //  Now That you have the list of PortfolioTrade And their data,
  //  With this data, Calculate annualized returns for the stocks provided in the Json
  //  Below are the values to be considered for calculations.
  //  buy_price = open_price on purchase_date and sell_value = close_price on end_date
  //  startDate and endDate are already calculated in module2
  //  using the function you just wrote #calculateAnnualizedReturns
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.
  //  use gralde command like below to test your code
  //  ./gradlew run --args="trades.json 2020-01-01"
  //  ./gradlew run --args="trades.json 2019-07-01"
  //  ./gradlew run --args="trades.json 2019-12-03"
  //  where trades.json is your json file
  public static final Comparator<AnnualizedReturn> arComparator = new 
      Comparator<AnnualizedReturn>() {         
    public int compare(AnnualizedReturn ar1, AnnualizedReturn ar2) {             
      return (ar2.getAnnualizedReturn() < ar1.getAnnualizedReturn() ? -1 :                     
        (ar2.getAnnualizedReturn().equals(ar2.getAnnualizedReturn()) ? 0 : 1));           
    }     
  }; 

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    List<PortfolioTrade> portfolioDataList = portfolioTrade(args);
    List<TiingoCandle> tiingoClassObj = new ArrayList<TiingoCandle>();
    RestTemplate restTemplate = new RestTemplate();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate endDate = LocalDate.parse(args[1], formatter);
    String token = "dcec30ee2bde0aef1bff3e7b63d0e539db020c7c";
    List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();
    AnnualizedReturn ar;
    Double buyPrice;
    Double sellPrice;
    List<LocalDate> endDList = listOfEndDate(tiingoClassObj);
    for (int j = 0;j < portfolioDataList.size();j++) {
      LocalDate startDate = portfolioDataList.get(j).getPurchaseDate();
      buyPrice = 0.0;
      sellPrice = 0.0;
      String ticker = portfolioDataList.get(j).getSymbol();
      String url = "https://api.tiingo.com/tiingo/daily/" + ticker + "/prices?startDate=" + startDate + "&endDate="
          + endDate + "&token=" + token;
      String result = restTemplate.getForObject(url, String.class);
      tiingoClassObj = getObjectMapper().readValue(result, 
          new TypeReference<List<TiingoCandle>>() {});
      for (int i = 0;i < tiingoClassObj.size();i++) {
        if (startDate.isEqual(tiingoClassObj.get(i).getDate())) {
          buyPrice = tiingoClassObj.get(i).getOpen();
        }
        if (!(endDList.contains(endDate))) {
          int k = tiingoClassObj.size() - 1;
          sellPrice = tiingoClassObj.get(k).getClose();
        } else {
          if (endDate.isEqual(tiingoClassObj.get(i).getDate())) {
            sellPrice = tiingoClassObj.get(i).getClose();
          }
        }
      }
      ar = calculateAnnualizedReturns(endDate,portfolioDataList.get(j),buyPrice,sellPrice);
      annualizedReturns.add(ar);
    }
    Collections.sort(annualizedReturns,arComparator);
    return annualizedReturns;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  annualized returns should be calculated in two steps -
  //  1. Calculate totalReturn = (sell_value - buy_value) / buy_value
  //  Store the same as totalReturns
  //  2. calculate extrapolated annualized returns by scaling the same in years span. The formula is
  //  annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //  Store the same as annualized_returns
  //  return the populated list of AnnualizedReturn for all stocks,
  //  Test the same using below specified command. The build should be successful
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    Double totalReturn = (sellPrice - buyPrice) / buyPrice;
    LocalDate startDate = trade.getPurchaseDate();
    Double daysBetween = ChronoUnit.DAYS.between(startDate, endDate) * 1.0;
    Double annualizedreturns = Math.pow(1 + totalReturn, 365 / daysBetween) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedreturns, totalReturn);
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    
    printJsonObject(mainCalculateSingleReturn(args));

  }
  // TODO: CRIO_TASK_MODULE_REST_API
  // Copy the relavent code from #mainReadFile to parse the Json into
  // PortfolioTrade list.
  // Now That you have the list of PortfolioTrade already populated in module#1
  // For each stock symbol in the portfolio trades,
  // Call Tiingo api
  // (https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=&endDate=&token=)
  // with
  // 1. ticker = symbol in portfolio_trade
  // 2. startDate = purchaseDate in portfolio_trade.
  // 3. endDate = args[1]
  // Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>
  // Note - You may have to register on Tiingo to get the api_token.
  // Please refer the the module documentation for the steps.
  // Find out the closing price of the stock on the end_date and
  // return the list of all symbols in ascending order by its close value on
  // endDate
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // And make sure that its printing correct results.

  public static final Comparator<TotalReturnsDto> priceComparator = new 
      Comparator<TotalReturnsDto>() {               
    public int compare(TotalReturnsDto tr1,TotalReturnsDto tr2) {             
        return (int) (tr1.getClosingPrice().compareTo(tr2.getClosingPrice()));          
    } 
  };

  public static List<String> mainReadQuotes(String[] args) throws URISyntaxException, IOException {
    String token = "dcec30ee2bde0aef1bff3e7b63d0e539db020c7c";
    List<TotalReturnsDto> trdObj = new ArrayList<TotalReturnsDto>();
    List<PortfolioTrade> portfolioDataList = portfolioTrade(args);
    RestTemplate restTemplate = new RestTemplate();
    priceList(args, token, trdObj, portfolioDataList, restTemplate);
    Collections.sort(trdObj,priceComparator);
    List<String> symbolList = new ArrayList<String>();
    for (int x = 0;x < trdObj.size();x++) {
      symbolList.add(trdObj.get(x).getSymbol());
    }
  
    return symbolList;
  }

  private static void priceList(String[] args, String token, List<TotalReturnsDto> trdObj,
      List<PortfolioTrade> portfolioDataList, RestTemplate restTemplate) throws 
          JsonProcessingException, JsonMappingException {
    for (int i = 0; i < portfolioDataList.size(); i++) {
      String ticker = portfolioDataList.get(i).getSymbol();
      LocalDate startDate = portfolioDataList.get(i).getPurchaseDate();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate endDate = LocalDate.parse(args[1], formatter);
      priceListAfterSorting(token, trdObj, restTemplate, ticker, startDate, endDate);
    }
  }

  private static void priceListAfterSorting(String token, List<TotalReturnsDto> trdObj,
      RestTemplate restTemplate,String ticker,LocalDate startDate, LocalDate endDate)
          throws JsonProcessingException, JsonMappingException {
    String url = "https://api.tiingo.com/tiingo/daily/" + ticker + "/prices?startDate=" + startDate + "&endDate="
        + endDate + "&token=" + token;
    String result = restTemplate.getForObject(url, String.class);
    List<TiingoCandle> tiingoClassObj = getObjectMapper().readValue(result, 
        new TypeReference<List<TiingoCandle>>() {});
    List<LocalDate> endDateList = listOfEndDate(tiingoClassObj);  
    if (startDate.isAfter(endDate)) {
      throw new RuntimeException();
    }
    if (endDateList.contains(endDate)) {
      for (int j = 0;j < tiingoClassObj.size();j++) {
        if (endDate.isEqual(tiingoClassObj.get(j).getDate())) {
          Double closingPrice = tiingoClassObj.get(j).getClose();
          TotalReturnsDto symbolPriceMap = new TotalReturnsDto(ticker,closingPrice);
          trdObj.add(symbolPriceMap);
        }
      }
    }
  }

  private static List<LocalDate> listOfEndDate(List<TiingoCandle> tiingoClassObj) {
    List<LocalDate> endDateList = new ArrayList<LocalDate>();
    for (int j = 0;j < tiingoClassObj.size();j++) {
      endDateList.add(tiingoClassObj.get(j).getDate());
    }
    return endDateList;
  }

  private static List<PortfolioTrade> portfolioTrade(String[] args)
      throws URISyntaxException, IOException, JsonParseException, JsonMappingException {
    File file = resolveFileFromResources(args[0]);
    TypeReference<List<PortfolioTrade>> classTypeRef = new TypeReference<List<PortfolioTrade>>() {
    };
    List<PortfolioTrade> portfolioDataList = getObjectMapper().readValue(file, classTypeRef);
    return portfolioDataList;
  }
}



