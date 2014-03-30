/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.portfolio.service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.samples.bo.Quote;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QuoteService implements
		ApplicationListener<BrokerAvailabilityEvent> {

	private static Log logger = LogFactory.getLog(QuoteService.class);

	private final MessageSendingOperations<String> messagingTemplate;

	private final StockQuoteGeneratorService quoteGenerator;

	private AtomicBoolean brokerAvailable = new AtomicBoolean();

	@Autowired
	public QuoteService(MessageSendingOperations<String> messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
		quoteGenerator = new StockQuoteGeneratorService();
		
	}

	public void onApplicationEvent(BrokerAvailabilityEvent event) {
		this.brokerAvailable.set(event.isBrokerAvailable());
	}

	@Scheduled(fixedDelay = 3000)
	//@Scheduled(cron="* * 9-16 * * MON-FRI")
	public void sendQuotes() throws Exception {
		this.quoteGenerator.generateStockQuotes();
		Thread.sleep(5000);
		// TODO check all updated in stockQuoteSet?
		for (Quote quote : this.quoteGenerator.getQuotesMap().values()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Sending quote " + quote);
			}
			if (this.brokerAvailable.get()) {
				this.messagingTemplate.convertAndSend("/topic/price.stock."
						+ quote.getTicker(), quote);
			}
		}
	}
	
	@Scheduled(cron="0 1 16 * * MON-FRI")
	public void updateDayClose() throws Exception {
		this.quoteGenerator.generateStockQuotes();
		Thread.sleep(5000);
		for (Quote quote : this.quoteGenerator.getQuotesMap().values()) {
			//DAO update day close 'day, ticker, close rate'
			logger.info("##########Day Close [ticker="+quote.getTicker()+", Date="+ new Date() + ", quote="+quote.getPrice() +"]");
		}
	}

	public boolean addStockTicker(String stockTicker) {
		if (this.quoteGenerator.getQuotesMap().get(stockTicker) == null) {
			this.quoteGenerator.getQuotesMap().put(stockTicker, new Quote());
			return true;
		}
		return false;
	}

	public StockQuoteGeneratorService getStockQuoteGenerator() {
		return this.quoteGenerator;
	}

	

}
