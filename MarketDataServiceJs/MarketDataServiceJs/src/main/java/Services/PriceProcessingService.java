package Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import MarketDataServiceJs.App;
import Data.DataHelper;
import Data.DataPoint;

public class PriceProcessingService implements Runnable {

	private BufferedReader reader;
	final CountDownLatch latch;
	private int secondCount = 0; 
	private DataHelper dataHelper; 
	
	public PriceProcessingService(DataHelper dataHelper, CountDownLatch latch) {
		this.dataHelper = dataHelper;
		this.latch = latch;
		InputStream res = App.class.getResourceAsStream("/input");
		reader = new BufferedReader(new InputStreamReader(res));
	}

	public void run() {
		
		secondCount++;
		int count = 4;
		String line;
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		
		try {
			while (count > 0) {
				// read line and check if need to cancel
				line = reader.readLine();
				if (line == null) {
					closeReader();
					return;
				}
				
				// decrement count and read from input file 
				count--;
				String[] arr = line.split(":");
				String name = arr[0];
				double price = Double.parseDouble(arr[1]);
				
				// do pre-processing for persistence
				if (shouldPersist(secondCount, name)) {
					dataPoints.add(new DataPoint(secondCount, name, price));
				}
				
				// logging to CLI
				String result = String.format("%1$d %2$s: %3$.4f", secondCount, name, price);
				System.out.println(result);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// persist if necessary
		if (dataPoints.size() > 0) {
			dataHelper.insertDataPoints(dataPoints);
		}
	}
	
	private void closeReader() throws IOException {
		reader.close();
		latch.countDown();
		throw new RuntimeException(); // exit
	}
	
	public static boolean shouldPersist(int second, String name) {

		if (second % 3 == 0 && (name.equals("GOOG") || name.equals("BP.L"))) {
			return true;
		}
		
		if (second % 5 == 0 && (name.equals("BT.L") || name.equals("VOD.L"))) {
			return true;
		}
		
		return false; 
	}

	public long getLatchCount() {
		return latch.getCount();
	}
}