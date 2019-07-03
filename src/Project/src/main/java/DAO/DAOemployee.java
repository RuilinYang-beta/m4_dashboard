package DAO;

import java.sql.*;
import org.apache.commons.codec.digest.DigestUtils;

public class DAOemployee {
	
	/**
	 * SHA256 hash: one way hashing function, can never read out email address
	 * @param message: to be hashed
	 */
	private static String hashString(String message) {
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
	
	/**
	 * Adds employee to database, doesn't require connection (creates it's own)
	 * @param name unimportant, just for security when deleting (cant accidently delete wrong person)
	 * @param id Has to be unique in the database
	 * @param mail Has to be unique in database, is saved SHA256-hashed
	 * @return String success for success and stacktrace for error (sql error: id or email not unique)
	 */
	public static String addEmployee(String name, int id, String mail) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		String z = "";
		try {
			PreparedStatement s = connection.prepareStatement("INSERT INTO employees(empId, name, email) VALUES(?,?,?);");
			s.setInt(1, id);
			s.setString(2, name);
			s.setString(3, hashString(mail));
			s.executeQuery();
			z = "success";
		} catch (SQLException e) {
			if (e.getMessage().equals("No results were returned by the query.")) {
				return "success";
			} else {
				System.err.println("error inserting");
				e.printStackTrace();
			}
			return (e.getMessage());
		} finally {
			try{connection.close();}catch(SQLException e){}
		}
		return z;
	}
	
	/**
	 * Check if email is in database (which gives access to settings.html webpage)
	 * @return true for "in database", false for "not in database"
	 */
	public static boolean checkEmployee(String mail) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		try {
			PreparedStatement s = connection.prepareStatement("SELECT COUNT(*) AS c FROM employees WHERE email=?;");
			s.setString(1,  hashString(mail));
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
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
	
	/**
	 * Delete function : have to know name and either ID or mail
	 * @return String success for success "not success" if name is not in database
	 */
	public static String deleteEmployee(String name, int id) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
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
	public static String deleteEmployee(String name, String mail) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
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
}