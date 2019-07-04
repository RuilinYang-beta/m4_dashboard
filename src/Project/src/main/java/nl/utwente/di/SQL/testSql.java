package nl.utwente.di.SQL;

import java.sql.*;
import DAO.*;

public class testSql {
	/**
	 * Allows for some database requests
	 * Automatically prints the result
	 * this function is used a lot to check if database insertion/deletion worked
	 */
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
