package nl.utwente.di.SQL;

import java.sql.*;

public class testSql {
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
		testSql d = new testSql();
		d.connectToDatabase();
		String command = "SELECT COUNT(dossierId) AS c FROM bookings where bookingId > 0;";
		try {
			Statement s = d.connection.createStatement();
			ResultSet rs = s.executeQuery(command);
			while (rs.next()) {
				System.out.println(rs.getInt("c"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			d.connection.close();
		} catch (SQLException e) {
			
		}
		
	}
}
