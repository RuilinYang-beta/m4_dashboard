package nl.utwente.di.SQL;

import java.sql.*;
import DAO.*;

public class Statistics {
	//this method parses all actions in our SQL database,
	//which are saved as JSON object
	//by extracting the data we want
	public static void parseActions(boolean RESET, int customer) {
		if (RESET) {
			System.out.println("reset");
			//execute(SQL_ACTIONS_PARSE + Database.OPT_ACT);
			DAOgeneral.execute("DELETE FROM st_actions WHERE id=" + customer);
		}
		StringBuffer execute = new StringBuffer();
		StringBuffer command = new StringBuffer();
		command.append("INSERT INTO st_actions (id, ");
		execute.append("SELECT id, ");
		String[] options = Database.OPT_ACT.split(",");
		for (int i = 0; i < options.length; i ++) {
			String temp = options[i].split(" ")[0];
			command.append(temp + ", ");
			String val = options[i].split(" ")[1];
			if (val.equals("VARCHAR(10));")) {
				val = "VARCHAR(10)";
			}
			System.out.println(temp);
			if (val.equals("TIMESTAMP")) {
				val = "BIGINT";
				execute.append("TO_TIMESTAMP(CAST(action::json->>'" + temp + "' AS " + val + ")/1000) AS " + temp + ", ");
			} else {
				execute.append("CAST(action::json->>'" + temp + "' AS " + val + ") AS " + temp + ", ");
			}
			
			
		}
		String res = command.substring(0, command.length()-2) + ") ";
		res += execute.substring(0, execute.length()-2);
		res += " FROM actions WHERE id=" + customer;
		System.out.println(res);
		DAOgeneral.execute(res);
	}
	
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
	
	private static final String SQL_ACTIONS_PARSE = "DROP TABLE IF EXISTS st_actions; CREATE TABLE st_actions (";
	public static final String SQL_BOOK = "DROP TABLE IF EXISTS st_book; CREATE TABLE st_book ("
			+ "date VARCHAR(7),"
			+ "counter INT);";
	public static final String SQL_BOOK_FILL = "INSERT INTO st_book (date, counter)"
				+ "SELECT CONCAT(cast(EXTRACT(year FROM createdOn) AS VARCHAR(4))"
				+ ",'_', RIGHT(CONCAT('0',cast(EXTRACT(month FROM createdOn) AS VARCHAR(2))), 2)) AS m_y, COUNT(bookingId) " //format YYYY_MM
				+ "FROM bookings "
				+ "GROUP BY m_y;";
	public static final String SQL_BOOK_UPDATE = "UPDATE st_book SET date = CONCAT(substring(date, 1, 5), '0',"
			+ " substring(date, 6, 1)) WHERE LENGTH(date) = 6;";
	public static final String SQL_COFANO_USERS = "DROP TABLE IF EXISTS employees; CREATE TABLE employees ("
			+ "name VARCHAR(100),"
			+ "empId INT NOT NULL PRIMARY KEY,"
			+ "email VARCHAR(100) NOT NULL UNIQUE);";
}
