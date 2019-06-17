package nl.utwente.di.SQL;

import java.sql.*;

public class Statistics {
	public Connection connection;
	
	public void connectToDatabase() {
		try {
			Class.forName("org.postgresql.Driver");
		}	catch (ClassNotFoundException cnfe) {
			System.err.println("Error loading driver: " + cnfe);
		}
		String host = "farm03.ewi.utwente.nl";
		String dbName = "docker";
		String password = "G6BzWOlT0S";
		String url = "jdbc:postgresql://"
		+ host + ":7035/" + dbName;
		
		try {
			connection = DriverManager.getConnection(url, dbName, password);
		}
		catch(SQLException sqle) {
			System.err.println("Error connecting: " + sqle);
		}
	}
	
	public static void main(String[] args) {
		Statistics s = new Statistics();
		s.connectToDatabase();
		ResultSet rs = s.execute("SELECT DISTINCT(orderState) as c FROM bookings;");
		try {
			while (rs.next()) {
				System.out.println(rs.getString("c"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		};
		//s.fillBooking_to_Date();
	}
	
	//Create table counting amount of bookings per month (createdOn date)
	public void fillBooking_to_Date() {
		execute(SQL_BOOK + SQL_BOOK_FILL);
		execute(SQL_BOOK_UPDATE);
	}
	
	public ResultSet execute(String command) {
		try {
			PreparedStatement s = connection.prepareStatement(command);
			ResultSet res =  s.executeQuery();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public int getCount() {
		ResultSet temp = execute("SELECT COUNT(*) AS a FROM bookings");
		try {
			while  (temp.next()) {
				return temp.getInt("a");
			}
		} catch (SQLException e) {
			return -1;
		}
		return -1;
	}
	
	private static final String SQL_BOOK = "DROP TABLE IF EXISTS st_book; CREATE TABLE st_book ("
			+ "date VARCHAR(7),"
			+ "counter INT);";
	private static final String SQL_BOOK_FILL = "INSERT INTO st_book (date, counter)"
				+ "SELECT CONCAT(cast(EXTRACT(year FROM createdOn) AS VARCHAR(4))"
				+ ",'_', cast(EXTRACT(month FROM createdOn) AS VARCHAR(2))) AS m_y, COUNT(bookingId) " //format YYYY_MM
				+ "FROM bookings "
				+ "GROUP BY m_y;";
	private static final String SQL_BOOK_UPDATE = "UPDATE st_book SET date = CONCAT(substring(date, 1, 5), '0', substring(date, 6, 1)) WHERE LENGTH(date) = 6;";
}
