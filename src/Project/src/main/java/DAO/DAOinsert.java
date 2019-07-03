package DAO;

import java.sql.*;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import nl.utwente.di.SQL.*;

public class DAOinsert {
	
	//insertion of actions into SQL
	public static void insertAction(List<String> data, int customer) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		StringBuffer execute = new StringBuffer();
		Statement s = null;
		for (String key:data) {
			execute.append("INSERT INTO actions (id, action) VALUES(" + customer + ", '" + key + "');");
		}
		try {
			s = connection.createStatement();
			s.execute(execute.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {connection.close();} catch(SQLException e) {}
		}
	}
	
	//insertion of locations in SQL
	public static void insertLocation(List<JSONObject> data, String act) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		String[] options = Database.OPT_LOC.split(",");
		String[] addOptions = Database.OPT_ADD.split(",");
		StringBuffer execute = new StringBuffer();
		PreparedStatement s = null;
		if (act.contentEquals("loc")) {
			for (JSONObject item:data) {
			execute.append("INSERT INTO locations VALUES(");
			for (int i = 1; i <= options.length; i ++) {
				boolean done = false;
				String c = options[i-1].split(" ")[0];
				try {
					if (item.getString(c).length() == 0) {
						execute.append("null" + ",");
						done = true;
					} else {
						execute.append("'" + item.getString(c) + "'" + ",");
						done = true;
					}
				} catch (JSONException z) {
					try {
						if (!done) {
							execute.append(item.getLong(c) + ",");
							done = true;
						}
						
					} catch (JSONException e) {
						if (!done) {
							execute.append("null" + ",");
						}
						
					}
				}
				
			}
			execute.setLength(execute.length()-1);
			execute.append(");");
		}
		}else {
			for (JSONObject item:data) {
			if (!item.get("address").toString().equals("null")) {
				execute.append("INSERT INTO address VALUES(");
				execute.append(item.getInt("locationId") + ",");
				for (int i = 2; i <= addOptions.length; i ++) {
					String c = addOptions[i-1].split(" ")[0];
					
					try {
						if (item.getJSONObject("address").getString(c).length() > 0) {
							execute.append("'" + item.getJSONObject("address").getString(c) + "'" + ",");
						} else if (item.getString(c).length() > 0) {
							execute.append("'" + item.getJSONObject("address").getString(c) + "'" + ",");
						} else {
							execute.append("null" + ",");
						}
					} catch (JSONException z) {
						try {
							execute.append(item.getJSONObject("address").getInt(c) + ",");
						} catch (JSONException e) {
							execute.append("null" + ",");
						}
					}
				}
				execute.setLength(execute.length()-1);
				execute.append(");");
			}		
			if (!item.get("address").toString().equals("null")) {
				execute.append("INSERT INTO address VALUES(");
				execute.append(item.getInt("locationId") + ",");
				for (int i = 2; i <= addOptions.length; i ++) {
					String c = addOptions[i-1].split(" ")[0];
					
					try {
						if (item.getJSONObject("address").getString(c).length() > 0) {
							execute.append("'" + item.getJSONObject("address").getString(c) + "'" + ",");
						} else if (item.getString(c).length() > 0) {
							execute.append("'" + item.getJSONObject("address").getString(c) + "'" + ",");
						} else {
							execute.append("null" + ",");
						}
					} catch (JSONException z) {
						try {
							execute.append(item.getJSONObject("address").getInt(c) + ",");
						} catch (JSONException e) {
							execute.append("null" + ",");
						}
					}
				}
				execute.setLength(execute.length()-1);
				execute.append(");");
			}
		}
		}
		try {
			System.out.println("Insert into database");
			s = connection.prepareStatement(execute.toString());
			s.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		} finally {
			if (s != null) {
				try {connection.close();}catch(SQLException e){}
			}
		}
		System.out.println("finished");
		}
	
	//insertion of linestops in SQL
	public static void insertLinestops(List<JSONObject> data) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		String[] options = Database.OPT_LINE.split(",");
		StringBuffer execute = new StringBuffer();
		PreparedStatement s = null;
		for(JSONObject item:data) {
			execute.append("INSERT INTO linestops VALUES(");
			for(int i = 1; i <= options.length; i ++) {
				String c = options[i-1].split(" ")[0];
				try {
					execute.append("'" + item.getString(c) + "'" + ",");
				} catch (JSONException d) {
					try {
						if (c.equals("sta") || c.equals("std")) {
							execute.append("'" + new Timestamp((long) item.get(c)) + "',");
						} else {
							execute.append(item.getInt(c) + ",");
						}
					} catch (JSONException e) {
						execute.append("null" + ",");
					} catch (ClassCastException cce) {
						execute.append("null" + ",");
					}
				}
			}
			execute.setLength(execute.length()-1);
			execute.append(");");
		}
		try {
			System.out.println("Insert into database");
			s = connection.prepareStatement(execute.toString());
			s.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		} finally {
			if (s != null) {
				try {connection.close();}catch(SQLException e){}
			}
		}
		System.out.println("finished Linestops");
	}
	
	//insertion of bookings in SQL
	public static void insertDatabase(List<JSONObject> data, String command, String opts) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		String[] options = opts.split(",");
		StringBuffer execute = new StringBuffer();
		PreparedStatement s = null;
		for (JSONObject item:data) {
			execute.append(command);
			for (int i = 1; i <= options.length; i ++) {
				String c = options[i-1].split(" ")[0];
				try {
					execute.append("'" + item.getString(c) + "'" + ",");
				} catch (JSONException d) {
					try {
						if (c.equals("createdOn") || c.equals("referenceDate")) {
							execute.append("'" + new Timestamp((long) item.get(c)) + "',");
						} else {
							execute.append(item.getInt(c) + ",");
						}
					} catch (JSONException e) {
						execute.append("null" + ",");
					} catch (ClassCastException cce) {
						execute.append("null" + ",");
					}
				}
			}
			execute.setLength(execute.length()-1);
			execute.append(");");
		}
		try {
			s = connection.prepareStatement(execute.toString());
			s.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		} finally {
			if (s != null) {
				try {connection.close();}catch(SQLException e){}
			}
		}
	}
	
	public static String insertCustomer(String name, String link) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		PreparedStatement stm = null;
		try {
			String command = "INSERT INTO customers (name, link) VALUES (?, ?)";
			stm = connection.prepareStatement(command);
			stm.setString(1, name);
			stm.setString(2, link);
		} catch (SQLException e) {
			return "value error";
		}
		try {
			stm.executeQuery();
			try {connection.close();} catch(SQLException e) {}
			return "WORKS";
		} catch (SQLException e) {
			try {connection.close();} catch(SQLException f) {}
			return e.toString();
		}
		
		
		
	}
}
