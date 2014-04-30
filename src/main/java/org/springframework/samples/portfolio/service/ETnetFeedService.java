package org.springframework.samples.portfolio.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.bo.Quote;
import org.springframework.samples.portfolio.service.IFeedService;

public class ETnetFeedService extends Observable implements IFeedService {

	private static Logger LOG = LoggerFactory.getLogger(ETnetFeedService.class);
	private static String FEED_URL = "https://www.etnet.com.hk/www/tc/stocks/realtime/quote.php?code=";
	private static String GET_METHOD = "GET";
	private static String FEED_PRICE_KEYWORD = "<span class=\"Price ";
	private static String START_PATTEN = "Price";
	private static String END_PATTEN = "&nbsp;";
	private String ticker;

	private boolean isETnetFeedServiceEnable = true;

	public ETnetFeedService() {
	}

	public void run() {
		startFeedService();
	}

	public void startFeedService() {
		if (isETnetFeedServiceEnable()) {

			String feedContent = httpGetFeedContent();

			String price = parseStockCurrentPrice(feedContent);

			Quote quote = generateEnrichedQuote(price);

			fireQuoteGeneratorEvent(quote);

		}
	}

	public String httpGetFeedContent() {
		String feedContent = "";
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
			feedContent = response.toString();
			LOG.info(String.format("Recevied feed content: %s", feedContent));
		} catch (Exception e) {
			LOG.warn("Http get process fail", e);
		}
		return feedContent;
	}

	protected String parseStockCurrentPrice(String feedContent) {
		String stockPrice = "";
		if (!feedContent.isEmpty()) {
			try {
				int start = feedContent.indexOf("\">",
						feedContent.indexOf(START_PATTEN));
				int end = feedContent.indexOf(END_PATTEN);
				stockPrice = feedContent.substring(start + 2, end);
			} catch (Exception e) {
				LOG.warn(String.format("FeedContent parse fail, Content=[%s]",
						feedContent));
			}
		} else {
			LOG.error("Feed content is empty");
		}
		return stockPrice;
	}

	public Quote generateEnrichedQuote(String price) {
		BigDecimal decPrice = new BigDecimal(price);
		Quote quote = new Quote(ticker, decPrice);
		return quote;
	}

	private void fireQuoteGeneratorEvent(Quote quote) {
		setChanged();
		notifyObservers(quote);
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public void stopFeedService() {
		setServiceStart(false);
	}

	public boolean isETnetFeedServiceEnable() {
		return isETnetFeedServiceEnable;
	}

	public void setServiceStart(boolean isServiceStart) {
		this.isETnetFeedServiceEnable = isServiceStart;
	}

	public void addFeedServiceObserver(Observer quoteGenerator) {
		super.addObserver(quoteGenerator);
	}

	public void deleteFeedServiceObserver(Observer quoteGenerator) {
		super.deleteObserver(quoteGenerator);
	}

}