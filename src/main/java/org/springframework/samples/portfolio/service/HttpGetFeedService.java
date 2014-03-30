package org.springframework.samples.portfolio.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.samples.bo.Quote;

class HttpGetFeedService implements Runnable {

	private static Log LOG = LogFactory.getLog(HttpGetFeedService.class);
	private static String FEED_URL = "https://www.etnet.com.hk/www/tc/stocks/realtime/quote.php?code=";
	private static String GET_METHOD = "GET";
	private static String FEED_PRICE_KEYWORD = "<span class=\"Price ";
	
	private StockQuoteGeneratorService stockQuoteGenerator;

	private String ticker;	

	public HttpGetFeedService(StockQuoteGeneratorService stockQuoteGenerator, String ticker) {
		this.stockQuoteGenerator = stockQuoteGenerator;
		this.ticker = ticker;
	}

	public void run() {
		try {
			String url = FEED_URL + Integer.parseInt(ticker);
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();			
			con.setRequestMethod(GET_METHOD);
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains(FEED_PRICE_KEYWORD)) {
					response.append(inputLine.trim());
				}
			}
			in.close();
			String feedContent = response.toString();
			LOG.info(String.format("Recevied feed content %s", feedContent));
			if (!feedContent.isEmpty()) {
				Quote quote = new Quote();
				quote.setTicker(ticker);
				quote.setFeedContent(feedContent);
				stockQuoteGenerator.deriveFeedContentToPrice(quote);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}