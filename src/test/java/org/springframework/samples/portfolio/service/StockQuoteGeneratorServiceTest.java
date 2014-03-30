package org.springframework.samples.portfolio.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class StockQuoteGeneratorServiceTest {

	StockQuoteGeneratorService stockQuoteGeneratorService;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		stockQuoteGeneratorService = new StockQuoteGeneratorService();
	}

	@Test
	public void testParseStockCurrentPriceWhenInvalidFeedContent() {
		// Given
		String feedContent = "abc";

		// When
		String stockPrice = stockQuoteGeneratorService
				.parseStockCurrentPrice(feedContent);

		// Then
		assertEquals("", stockPrice);
	}

}
