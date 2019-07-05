package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAOgeneral {
	/**
	 * Static function, doesn't need DAO inititalization
	 * @param connection is null on start, will be returned as actual connection
	 */
	public static Connection connectToDatabase(Connection connection) {
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
			return connection;
		}
		catch(SQLException sqle) {
			System.err.println("Error connecting: " + sqle);
			return null;
		}
	}
	
	//executes a query and returns resultset 
	public static ResultSet execute(String command) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		try {
			PreparedStatement s = connection.prepareStatement(command);
			ResultSet res =  s.executeQuery();
			return res;
		} catch (SQLException e) {
			if (e.getMessage().equals("No results were returned by the query.")) {
				System.err.println("this query has no resultset");
				return null;
			} else {
				e.printStackTrace();
				return null;
			}
		} finally {
			try {connection.close();}catch(SQLException e) {}
		}
		
	}
	
	/**
	 * Get amount of bookings from server
	 * @return
	 */
	public static int getCount() {
		ResultSet temp = DAOgeneral.execute("SELECT COUNT(*) AS a FROM bookings");
		try {
			while  (temp.next()) {
				return temp.getInt("a");
			}
		} catch (SQLException e) {
			return -1;
		}
		return -1;
	}
}
