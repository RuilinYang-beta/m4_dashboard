package nl.utwente.di.SQL;

import java.sql.*;
import java.io.IOException;
import java.net.*;
import java.io.*;
import java.time.Instant;

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
	
	public static void maino(String[] args) {
		testSql d = new testSql();
		d.connectToDatabase();
		String command = "SELECT COUNT(DISTINCT a.locationId) AS c, COUNT(DISTINCT b.locationId) AS d FROM locations a, address b;";
		try {
			Statement s = d.connection.createStatement();
			ResultSet rs = s.executeQuery(command);
			while (rs.next()) {
				System.out.println(rs.getInt("c"));
				System.out.println(rs.getInt("d"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			d.connection.close();
		} catch (SQLException e) {
			
		}
	}
	
	public static void main(String[] args) {
		testSql d = new testSql();
		d.connectToDatabase();
		d.createStatisticsTable("name");
		try {
			d.connection.close();
		} catch (SQLException e) {
			
		}
	}
	
	public void createStatisticsTable(String name) {
//		String command = "CREATE TABLE " + name + " (month TIMSTAMP, amount INT)";
//		try {
//			Statement s = connection.createStatement();
//			s.executeQuery(command);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		
//		String command = "SELECT MIN(createdOn) AS c FROM bookings;";
//		Timestamp date = null;
//		try {
//			Statement s = connection.createStatement();
//			ResultSet rs = s.executeQuery(command);
//			while (rs.next()) {
//				date = (Timestamp) rs.getObject("c");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		System.out.println(date);
		
		URL url;
		HttpURLConnection con = null;
		try {
			Instant instant = Instant.now();
			long timeStampMillis = instant.toEpochMilli();
			url = new URL("http://localhost:8080/Project/rest/sql/select?from=1253788820&to=1503788820&dosId=0");
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			System.out.println(con.getResponseCode());
			BufferedReader in = new BufferedReader(
			new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			System.out.println(content.toString());
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
}
