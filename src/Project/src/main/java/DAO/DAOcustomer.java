package DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DAOcustomer {
	/**
	 * Get the link of a customer
	 * @param customer Id of a customer
	 * @return String of the link for 'link in database' and 'doesn't exists' for error and empty String for 'link not in database'
	 */
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

	/**
	 * Get the id of a customer
	 * @param customer The name of customer
	 * @return Integer id for 'id in database' and -1 for errors and for 'id not in database'
	 */
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
