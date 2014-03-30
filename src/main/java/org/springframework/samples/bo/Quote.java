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
package org.springframework.samples.bo;

import java.math.BigDecimal;

public class Quote {

	private String ticker;
	private BigDecimal price;
	private String feedContent;

	public Quote() {
		this.ticker = "";
		this.price = new BigDecimal(0);
		this.feedContent = "";
	}

	public Quote(String ticker, BigDecimal price) {
		this.ticker = ticker;
		this.price = price;
		this.feedContent = "";
	}

	public String getTicker() {
		return this.ticker;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public String getFeedContent() {
		return this.feedContent;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setFeedContent(String feedContent) {
		this.feedContent = feedContent;
	}

	@Override
	public String toString() {
		return "Quote [ticker=" + this.ticker + ", this.price=" + price + "]";
	}
}
