package nl.utwente.di.SQL;

import java.sql.*;
import DAO.*;

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
		String command = "SELECT * FROM customers";
		try {
			ResultSet rs = DAOgeneral.execute(command);
			if (rs != null) {
				ResultSetMetaData rsdm = rs.getMetaData();
				while (rs.next()) {
					for (int i = 1; i <= rsdm.getColumnCount(); i ++) {
						System.out.println(rsdm.getColumnLabel(i) + " " + rs.getString(i));
					}
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
