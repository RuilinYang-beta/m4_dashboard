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
		d.Test();
		
		try {
			d.connection.close();
		} catch (SQLException e) {
			
		}
	}
	
	public void Test() {
		//String command = "SELECT action::json->'bookingId' AS c FROM actions LIMIT 20; ";
		//String command = "SELECT CAST(action::json->>'bookingId' AS INT) AS bookingId, CAST(action::json->>'bookingIdentifier' AS VARCHAR(10)) AS bookingIdentifier, CAST(action::json->>'actionId' AS INT) AS actionId, CAST(action::json->>'actionType' AS VARCHAR(20)) AS actionType, CAST(action::json->>'deletedOn' AS TIMESTAMP) AS deletedOn, CAST(action::json->>'index' AS INT) AS index, CAST(action::json->>'status' AS VARCHAR(20)) AS status, CAST(action::json->>'sta' AS TIMESTAMP) AS sta, CAST(action::json->>'std' AS TIMESTAMP) AS std, CAST(action::json->>'startLocationId' AS INT) AS startLocationId, CAST(action::json->>'startLocationType' AS VARCHAR(20)) AS startLocationType, CAST(action::json->>'startLocationName' AS VARCHAR(63)) AS startLocationName, CAST(action::json->>'startLocationAddress' AS VARCHAR(63)) AS startLocationAddress, CAST(action::json->>'startLocationCity' AS VARCHAR(63)) AS startLocationCity, CAST(action::json->>'endLocationId' AS INT) AS endLocationId, CAST(action::json->>'endLocationType' AS VARCHAR(63)) AS endLocationType, CAST(action::json->>'endLocationName' AS VARCHAR(63)) AS endLocationName, CAST(action::json->>'endLocationAddress' AS VARCHAR(63)) AS endLocationAddress, CAST(action::json->>'endLocationCity' AS VARCHAR(63)) AS endLocationCity, CAST(action::json->>'loadLinestopId' AS INT) AS loadLinestopId, CAST(action::json->>'unloadLinestopId' AS INT) AS unloadLinestopId, CAST(action::json->>'shipName' AS VARCHAR(63)) AS shipName, CAST(action::json->>'locationId' AS INT) AS locationId, CAST(action::json->>'locationType' AS VARCHAR(63)) AS locationType, CAST(action::json->>'locationName' AS VARCHAR(63)) AS locationName, CAST(action::json->>'locationAddress' AS VARCHAR(63)) AS locationAddress, CAST(action::json->>'locationCity' AS VARCHAR(63)) AS locationCity, CAST(action::json->>'gateIn' AS VARCHAR(63)) AS gateIn, CAST(action::json->>'gateOut' AS VARCHAR(63)) AS gateOut, CAST(action::json->>'applicationTerminal' AS VARCHAR(10));) AS applicationTerminal FROM actions";
//		String command = "CREATE TABLE customers (name VARCHAR(50), id INT);"
//				+ "INSERT INTO customers VALUES('customer1', 1);"
//				+ "INSERT INTO customers VALUES('customer2', 2);";
		String command = "UPDATE actions SET id=2;";
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery(command);
			ResultSetMetaData rsdm = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= rsdm.getColumnCount(); i ++) {
					System.out.println(rsdm.getColumnLabel(i) + " " + rs.getString(i));
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
