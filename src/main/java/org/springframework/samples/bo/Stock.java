package org.springframework.samples.bo;

public class Stock {

	private String stockTicker;

	public String getStockTicker() {
		return stockTicker;
	}

	public void setStockTicker(String stockTicker) {
		this.stockTicker = stockTicker;
	}

	@Override
	public String toString() {
		return "Stock [stockTicker=" + stockTicker + "]";
	}

}
