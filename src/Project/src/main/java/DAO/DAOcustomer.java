package DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DAOcustomer {
	public static String getCustLink(int customer) {
		Connection c = null;
		c = DAOgeneral.connectToDatabase(c);
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT link FROM customers WHERE id=" + customer);
			while (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			return "doesn't exists";
		}
		
		return "";
	}
	
	public static int getCustId(String customer) {
		Connection c = null;
		c = DAOgeneral.connectToDatabase(c);
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT id FROM customers WHERE name='" + customer + "'");
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			return -1;
		}
		return -1;
	}
}
