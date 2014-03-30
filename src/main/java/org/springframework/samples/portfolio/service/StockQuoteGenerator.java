package org.springframework.samples.portfolio.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.samples.bo.Quote;

public class StockQuoteGenerator {

	private static Log LOG = LogFactory.getLog(StockQuoteGenerator.class);
	private static String START_PATTEN = "Price";
	private static String END_PATTEN = "&nbsp;";

	ExecutorService feedThreadPool = Executors.newFixedThreadPool(5);

	private Map<String, Quote> quotesMap = new ConcurrentHashMap<>();

	public StockQuoteGenerator() {
		quotesMap.put("5", new Quote());
		quotesMap.put("941", new Quote());
		quotesMap.put("2883", new Quote());
	}

	public Map<String, Quote> getQuotesMap() {
		return quotesMap;
	}

	public void generateStockQuotes() {
		for (String ticker : quotesMap.keySet()) {
			HttpGetFeedService feedService = new HttpGetFeedService(this,
					ticker);
			feedThreadPool.execute(feedService);
		}
	}

	public void deriveFeedContentToPrice(Quote quote) {
		String stockPrice = parseStockCurrentPrice(quote.getFeedContent());
		updateStockQuoteMapPrice(quote, stockPrice);
	}

	public String parseStockCurrentPrice(String feedContent) {
		String stockPrice = "";
		if (!feedContent.isEmpty()) {
			try {
				int start = feedContent.indexOf("\">",
						feedContent.indexOf(START_PATTEN));
				int end = feedContent.indexOf(END_PATTEN);
				stockPrice = feedContent.substring(start + 2, end);
			} catch (Exception e) {
				LOG.error(String.format("FeedContent parse fail, Content=[%s]",
						feedContent));
				e.printStackTrace();
			}
		} else {
			LOG.error("Feed content is empty");
		}
		return stockPrice;
	}

	private void updateStockQuoteMapPrice(Quote quote, String stockPrice) {
		try {
			BigDecimal price = new BigDecimal(stockPrice);
			quote.setPrice(price);
			quotesMap.put(quote.getTicker(), quote);
			LOG.info(String.format("Updated quotes map: %s", quote));
		} catch (NumberFormatException e) {
			LOG.info(String.format("Ignore quote: %s", quote.getTicker()));
		}
	}

}
