package DAO;

import java.sql.*;

import nl.utwente.di.SQL.*;


public class DAOupdate {
	public static void updateBook(boolean RESET, int customer) {
		if (!RESET) {
			Connection connection = null;
			connection = DAOgeneral.connectToDatabase(connection);
			Long offBook = null;
			String command = "SELECT MAX(createdOn) AS c FROM bookings WHERE id= " + customer;
			Timestamp x = null;
			try {
				Statement s = connection.createStatement();
				ResultSet rs = s.executeQuery(command);
				while (rs.next()) {
					x = (Timestamp)rs.getObject("c");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					
				}
			}
			
			if (x != null) {
				offBook = x.getTime();
				Database.makeTable("bookings", RESET, offBook/1000+1,  customer);
			} else {
				Database.makeTable("bookings", RESET, 0, customer);
			}
		} else {
			Database.makeTable("bookings", RESET, 0, customer);
		}
	}
	public static void updateLoc(int customer) {
		Database.makeTable("locations", true, 0, customer);
	}
	public static void updateLines(boolean RESET, int customer) {
		if (!RESET) {
			String command = "SELECT MAX(sta) AS c FROM linestops WHERE id=" + customer;
			Timestamp x = null;
			Connection connection = null;
			connection = DAOgeneral.connectToDatabase(connection);
			try {
				Statement s = connection.createStatement();
				ResultSet rs = s.executeQuery(command);
				while (rs.next()) {
					x = (Timestamp)rs.getObject("c");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					
				}
			}
			
			Database.makeTable("linestops", RESET, (x.getTime())/1000, customer);	
		} else {
			Database.makeTable("linestops", RESET, new Long(0), customer);
		}
		
	}
	public static void updateActions(boolean RESET, int customer) {
		Database.makeTable("actions", RESET, 0, customer);
		Statistics.parseActions(true, customer);
	}
}
