package nl.utwente.di.SQL;

import java.sql.*;
import org.apache.commons.codec.digest.DigestUtils;

public class Statistics {
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
	
	//hash function for email address
	public static String hashString(String message) {
		String res = "";
		try {
			res = DigestUtils.sha256Hex(message);
		} catch (NullPointerException e) {
			
			DigestUtils du = new DigestUtils();
			res=du.sha256Hex(message);
			System.out.println("NULLPOINT " + res);
		}
		return res;
	}
	
	//all Employee-functions require email as string, NOT hashed
	//returns "success" if successful, else returns error code
	public String addEmployee(String name, int id, String mail) {
		connectToDatabase();
		String z = "";
		try {
			PreparedStatement s = connection.prepareStatement("INSERT INTO employees(empId, name, email) VALUES(?,?,?);");
			s.setInt(1, id);
			s.setString(2, name);
			s.setString(3, hashString(mail));
			s.executeQuery();
			z = "success";
		} catch (SQLException e) {
			System.err.println("error inserting");
			e.printStackTrace();
			if (e.getMessage().equals("No results were returned by the query.")) {
				return "success";
			}
			return (e.getMessage());
		} finally {
			try{connection.close();}catch(SQLException e){}
		}
		return z;
	}
	//returns true if email is in database
	public boolean checkEmployee(String mail) {
		connectToDatabase();
		try {
			PreparedStatement s = connection.prepareStatement("SELECT COUNT(*) AS c FROM employees WHERE email=?;");
			s.setString(1,  hashString(mail));
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getInt(1));
				if (rs.getInt("c") > 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try{connection.close();}catch(SQLException e){}
		}
		return false;
	}
	//either have to know name and mail or name and id to delete
	public String deleteEmployee(String name, int id) {
		connectToDatabase();
		try {
			PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) AS c FROM employees WHERE name=? AND empId=?;");
			st.setString(1,  name);
			st.setInt(2,  id);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					return "not success";
				}
			}
			PreparedStatement s = connection.prepareStatement("DELETE FROM employees WHERE name=? AND empId=?;");
			s.setString(1,  name);
			s.setInt(2,  id);
			s.executeQuery();
		} catch (SQLException e) {
			System.err.println("error inserting");
			e.printStackTrace();
			if (e.getMessage().equals("No results were returned by the query.")) {
				return "success";
			}
			return (e.getMessage());
		} finally {
			try{connection.close();}catch(SQLException e){}
		}
		return "success";
	}
	public String deleteEmployee(String name, String mail) {
		connectToDatabase();
		try {
			PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) AS c FROM employees WHERE name=? AND email=?;");
			st.setString(1,  name);
			st.setString(2,  hashString(mail));
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					return "not success";
				}
			}
			PreparedStatement s = connection.prepareStatement("DELETE FROM employees WHERE name=? AND email=?;");
			s.setString(1,  name);
			s.setString(2,  hashString(mail));
			s.executeQuery();
		} catch (SQLException e) {
			System.err.println("error inserting");
			e.printStackTrace();
			if (e.getMessage().equals("No results were returned by the query.")) {
				return "success";
			}
			return (e.getMessage());
		} finally {
			try{connection.close();}catch(SQLException e){}
		}
		return "success";
	}

	public static void main(String[] args) {
		Statistics s = new Statistics();
		s.connectToDatabase();
		//ResultSet rs = s.execute("SELECT DISTINCT(orderState) as c FROM bookings;");
		s.parseActions(true, 1);
		try {
			s.connection.close();
		} catch (SQLException e) {
			
		}
		//s.fillBooking_to_Date();
	}
		
	public void parseActions(boolean RESET, int customer) {
		if (RESET) {
			System.out.println("reset");
			//execute(SQL_ACTIONS_PARSE + Database.OPT_ACT);
			execute("DELETE FROM st_actions WHERE id=" + customer);
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
		execute(res);
	}
	
	public void parseTasks(boolean RESET, int customer) {
		
	}

	public ResultSet execute(String command) {
		try {
			PreparedStatement s = connection.prepareStatement(command);
			ResultSet res =  s.executeQuery();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public int getCount() {
		ResultSet temp = execute("SELECT COUNT(*) AS a FROM bookings");
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
