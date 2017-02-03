package MarketDataServiceJs;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.junit.Assert;
import Data.DataHelper;
import Services.PriceProcessingService;

public class UnitTests {

	@Test
    public void Test_DataHelper_Is_Correctly_Loaded() {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("Beans.xml");
		DataHelper dataHelper = (DataHelper) appContext.getBean("dataHelper");
		
		Assert.assertNotNull(dataHelper);
    }
	
	@Test
    public void Test_PriceProcessingService_Persistance_Criteria() {
		
		// arrange
		String bp = "BP.L"; // every 3 seconds
		String vod = "VOD.L"; // every 5 seconds
		
		// act and assert
		Assert.assertFalse(PriceProcessingService.shouldPersist(1, bp));
		Assert.assertFalse(PriceProcessingService.shouldPersist(11, vod));
		Assert.assertTrue(PriceProcessingService.shouldPersist(12, bp));
		Assert.assertTrue(PriceProcessingService.shouldPersist(15, vod));
    }
	
	@Test
	public void Test_CountdownLatch_IsCounted() {
		
		// arrange
		ApplicationContext appContext = new ClassPathXmlApplicationContext("Beans.xml");
		DataHelper dataHelper = (DataHelper) appContext.getBean("dataHelper");
		Assert.assertNotNull(dataHelper);
		CountDownLatch latch = new CountDownLatch(1);
		PriceProcessingService pps = new PriceProcessingService(dataHelper, latch);
		
		// act
		try {
			while(true) {
				pps.run();
			}
		} catch (RuntimeException exception) {
			
		}
		
		// assert
		Assert.assertEquals(0, pps.getLatchCount());
	}
}

