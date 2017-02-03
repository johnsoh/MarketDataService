package Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class DataHelper {

	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public synchronized void insertDataPoints(List<DataPoint> dataPoints) {

		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < dataPoints.size(); i++) {
			DataPoint dataPoint = dataPoints.get(i);
			sb.append(String.format("INSERT INTO prices VALUES (%1$d, '%2$s', %3$.4f); ", dataPoint.time, dataPoint.stockName, dataPoint.price));
		}
		
		jdbcTemplate.execute(sb.toString());
		
		for(DataPoint dataPoint : dataPoints) {
			System.out.println(String.format("%1$d %2$s persisted", dataPoint.time, dataPoint.stockName));
		}
	}

	public synchronized void createTables() {
		String sqlCreate = "DROP TABLE IF EXISTS prices; CREATE TABLE prices (time INT NOT NULL, stock_name VARCHAR(20) NOT NULL, price DECIMAL(32,8) NOT NULL);";
		jdbcTemplate.execute(sqlCreate);
	}

	public synchronized double getAveragePersistedPrice(int startingTimeHorizon) {

		String queryString = String.format("SELECT avg(price) as averagePrice FROM prices WHERE time >= %1$d", startingTimeHorizon);
		double averagePrice = jdbcTemplate.queryForObject(queryString, Double.class);
		return averagePrice;
	}

	public synchronized StringBuilder getSecondHighestPersistedPrice() {

		String queryString = 	"select max(p1.price) secondPrice, p1.stock_name from prices p1, " + 
								"(SELECT max(price) maxPrice, stock_name FROM prices GROUP BY stock_name) p2 " + 
								"WHERE p1.stock_name = p2.stock_name AND p1.price < p2.maxPrice GROUP BY p1.stock_name;";
		
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(queryString);
		for(Map<String, Object> map : rows) {
			String stockName = (String) map.get("stock_name");
			BigDecimal price = (BigDecimal) map.get("secondPrice");
			sb.append(String.format("%1$s: %2$.4f", stockName, price.doubleValue()));
			sb.append(System.lineSeparator());
		}
		
		return sb;
	}
}
