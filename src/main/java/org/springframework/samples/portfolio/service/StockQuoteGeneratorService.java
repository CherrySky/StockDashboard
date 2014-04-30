package org.springframework.samples.portfolio.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.bo.Quote;

public class StockQuoteGeneratorService implements Observer {

	private static Logger LOG = LoggerFactory
			.getLogger(StockQuoteGeneratorService.class);

	ExecutorService feedThreadPool = Executors.newFixedThreadPool(5);

	private Map<String, Quote> quotesMap = new ConcurrentHashMap<String, Quote>();
	
	private Map<String, IFeedService> feedServiceCacheMap = new HashMap<String, IFeedService>();
	
	public StockQuoteGeneratorService() {
		quotesMap.put("5", new Quote());
		quotesMap.put("941", new Quote());
		quotesMap.put("2883", new Quote());
	}	

	public void generateStockQuotes() {
		for (String ticker : quotesMap.keySet()) {
			if (!feedServiceCacheMap.containsKey(ticker)) {
				//TODO injected by Spring
				IFeedService feedService = new ETnetFeedService();			
				feedService.setTicker(ticker);
				addFeedServiceObserver(feedService);
				feedServiceCacheMap.put(ticker, feedService);
			} 
			
			IFeedService feedService = feedServiceCacheMap.get(ticker);
			feedThreadPool.execute(feedService);
		}
	}
	
	public void update(Observable o, Object quote) {
		if (!(quote instanceof Quote)) {
			LOG.warn("Non-Quote is processed");
			return;
		}
		LOG.info(String.format("%s updated, %s", o.getClass().getName(),
				quote.toString()));
		updateStockQuoteMapPrice((Quote) quote);
	}	
	
	protected void updateStockQuoteMapPrice(Quote quote) {
		try {
			getQuotesMap().put(quote.getTicker(), quote);
			LOG.info(String.format("Updated quotes map: %s", quote));
		} catch (NumberFormatException e) {
			LOG.info(String.format("Ignore quote: %s", quote.getTicker()));
		}
	}
	
	public Map<String, Quote> getQuotesMap() {
		return quotesMap;
	}
	
	protected void addFeedServiceObserver(IFeedService feedService) {
		feedService.addFeedServiceObserver(this);
	}

	protected void deleteFeedServiceObserver(IFeedService feedService) {
		feedService.deleteFeedServiceObserver(this);
	}

	
}
