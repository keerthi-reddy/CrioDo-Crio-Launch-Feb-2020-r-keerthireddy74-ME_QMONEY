package com.crio.warmup.stock;
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
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

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
    List<PortfolioTrade> portfolioDataList = portdolioTrade(args);
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
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
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

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadQuotes(args));

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
  public static Comparator<TotalReturnsDto> priceComparator = new Comparator<TotalReturnsDto>() {               
    public int compare(TotalReturnsDto tr1,TotalReturnsDto tr2) {             
     return (int) (tr1.getClosingPrice().compareTo(tr2.getClosingPrice()));          
    } 
  };
  public static List<String> mainReadQuotes(String[] args) throws URISyntaxException, IOException {
    String token = "dcec30ee2bde0aef1bff3e7b63d0e539db020c7c";
    List<TotalReturnsDto> trdObj=new ArrayList<TotalReturnsDto>();
    List<PortfolioTrade> portfolioDataList = portdolioTrade(args);
    RestTemplate restTemplate = new RestTemplate();
    priceList(args, token, trdObj, portfolioDataList, restTemplate);
    Collections.sort(trdObj,priceComparator);
    List<String> symbolList = new ArrayList<String>();
    for (int x=0;x<trdObj.size();x++) {
      symbolList.add(trdObj.get(x).getSymbol());
    }
  
    return symbolList;
  }

  private static void priceList(String[] args, String token, List<TotalReturnsDto> trdObj,
      List<PortfolioTrade> portfolioDataList, RestTemplate restTemplate)
      throws JsonProcessingException, JsonMappingException {
    for (int i = 0; i < portfolioDataList.size(); i++) {
      String ticker = portfolioDataList.get(i).getSymbol();
      LocalDate startDate = portfolioDataList.get(i).getPurchaseDate();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate endDate = LocalDate.parse(args[1], formatter);
      priceListAfterSorting(token, trdObj, restTemplate, ticker, startDate, endDate);
  }
  }

  private static void priceListAfterSorting(String token, List<TotalReturnsDto> trdObj, RestTemplate restTemplate, String ticker,
      LocalDate startDate, LocalDate endDate) throws JsonProcessingException, JsonMappingException {
    String url = "https://api.tiingo.com/tiingo/daily/" + ticker + "/prices?startDate=" + startDate + "&endDate="
        + endDate + "&token=" + token;
    String result = restTemplate.getForObject(url, String.class);
    List<TiingoCandle> tiingoClassObj = getObjectMapper().readValue(result, new TypeReference<List<TiingoCandle>>() {
    });
    for(int j=0;j<tiingoClassObj.size();j++)
    {
    if (startDate.isBefore(endDate)&&endDate.isEqual(tiingoClassObj.get(j).getDate())) {
      TotalReturnsDto symbolPriceMap=new TotalReturnsDto(ticker,tiingoClassObj.get(j).getClose() );
      trdObj.add(symbolPriceMap);
    }
  }
  }

  private static List<PortfolioTrade> portdolioTrade(String[] args)
      throws URISyntaxException, IOException, JsonParseException, JsonMappingException {
    File file = resolveFileFromResources(args[0]);
    TypeReference<List<PortfolioTrade>> classTypeRef = new TypeReference<List<PortfolioTrade>>() {
    };
    List<PortfolioTrade> portfolioDataList = getObjectMapper().readValue(file, classTypeRef);
    return portfolioDataList;
  }
  

}
