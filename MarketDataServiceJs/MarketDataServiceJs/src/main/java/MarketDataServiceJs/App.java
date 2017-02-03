package MarketDataServiceJs;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import Data.DataHelper;
import Services.PriceProcessingService;
import Services.ReportingService;

public class App 
{	
	public static final int DELAY = 30;
	public static final int POLLING_FREQUENCY = 1;
	public static final int NUM_OF_THREADS = 2;
	public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
	
	public static void main(String[] args) throws IOException, InterruptedException
	{	
		ApplicationContext appContext = new ClassPathXmlApplicationContext("/Beans.xml");
		DataHelper dataHelper = (DataHelper) appContext.getBean("dataHelper");
		dataHelper.createTables();
		
		ScheduledExecutorService es = Executors.newScheduledThreadPool(NUM_OF_THREADS);
		final CountDownLatch latch = new CountDownLatch(NUM_OF_THREADS);
		
		es.scheduleAtFixedRate(new PriceProcessingService(dataHelper, latch), 0, POLLING_FREQUENCY, TIME_UNIT);
		es.scheduleWithFixedDelay(new ReportingService(dataHelper, latch, DELAY), DELAY, 1, TIME_UNIT);
		
		latch.await();
		es.shutdown();
	}
}