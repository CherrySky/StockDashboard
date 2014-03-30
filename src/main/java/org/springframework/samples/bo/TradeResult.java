package org.springframework.samples.bo;

public class TradeResult {

	public final String user;
	public final PortfolioPosition position;
	public final long timestamp;

	public TradeResult(String user, PortfolioPosition position) {
		this.user = user;
		this.position = position;
		this.timestamp = System.currentTimeMillis();
	}
}