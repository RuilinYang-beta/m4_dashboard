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
		s.fillBookingDate();
		try {
			ResultSet rs = s.execute("SELECT SUM(counter) AS c FROM st_book");
			while (rs.next()) {
				System.out.println(rs.getInt("c"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		};
	}
	
	public void fillBookingDate() {
		execute(SQL_BOOK + SQL_BOOK_FILL);
	}
	
	public ResultSet execute(String command) {
		try {
			PreparedStatement s = connection.prepareStatement(command);
			ResultSet res =  s.executeQuery();
			return res;
		} catch (SQLException e) {
			return null;
		}
		
	}
	
	private static final String SQL_BOOK = "DROP TABLE IF EXISTS st_book; CREATE TABLE st_book ("
			+ "date VARCHAR(7),"
			+ "counter INT);";
	private static final String SQL_BOOK_FILL = "INSERT INTO st_book (date, counter)"
				+ "SELECT CONCAT(cast(EXTRACT(month FROM createdOn) AS VARCHAR(2))"
				+ ",'_', cast(EXTRACT(year FROM createdOn) AS VARCHAR(4))) AS m_y, COUNT(bookingId) "
				+ "FROM bookings "
				+ "GROUP BY m_y;";
}
