package org.springframework.samples.portfolio.service;

import java.util.Observer;

public interface IFeedService extends Runnable {

	public void startFeedService();

	public void stopFeedService();

	public void setTicker(String ticker);

	public void addFeedServiceObserver(Observer quoteGenerator);
	
	public void deleteFeedServiceObserver(Observer quoteGenerator);

}
