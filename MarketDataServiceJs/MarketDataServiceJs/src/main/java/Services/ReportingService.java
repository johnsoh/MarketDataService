package Services;

import java.util.concurrent.CountDownLatch;

import Data.DataHelper;

public class ReportingService implements Runnable {

	private DataHelper dataHelper;
	private CountDownLatch latch;
	private int delay;
	
	public ReportingService(DataHelper dataHelper, CountDownLatch latch, int delay) {
		this.dataHelper = dataHelper;
		this.latch = latch;
		this.delay = delay;
	}
	
	public void run() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(System.lineSeparator());
		sb.append("===== Report Begin =====");
		sb.append(System.lineSeparator());
		
		double average = dataHelper.getAveragePersistedPrice(delay - 10);
		sb.append("Average Persisted Price:" +  average);
		sb.append(System.lineSeparator());
		
		StringBuilder secondHighestData = dataHelper.getSecondHighestPersistedPrice();
		sb.append("===== Report: 2nd Highest Price =====");
		sb.append(System.lineSeparator());
		sb.append(secondHighestData.toString());
		
		System.out.println(sb.toString());
		latch.countDown();
	}
}
