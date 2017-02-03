package Data;

public class DataPoint {

	public DataPoint(int time, String stockName, double price) {
		this.time = time;
		this.stockName = stockName;
		this.price = price; 
	}
	
	public String stockName;
	public int time;
	public double price;
}

