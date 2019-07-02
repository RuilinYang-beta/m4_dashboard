package nl.utwente.di.SQL;

import java.sql.*;
import java.util.Date;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONException;

public class Database {
	public Connection connection;
	private static final String GET_URL = "https://module4t2-test.cofanostack.com/api/bigbrother/";
	private static final String AUTH_CODE = "Basic QmlnQnJvdGhlcjpob3R0ZW50b3R0ZW50ZW50ZW50ZW50b29uc3RlbGxpbmc=";
	
	//Standard connection: execute this to open the public variable connection
	//connects to the SQL database
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
	
	//Instantiates HTTP request and SQL insertion for the database
	//requires 4 variables:
	//path=table, so either "locations", "bookings", "linestops", or "actions"
	//reset=reset the table, ergo creates a new one
	//offset=start value, if you dont reset table, we dont need all new info
	//offset can be either int or Long
	//customer=customer id to keep table unique
	public void makeTable(String path, boolean RESET, Object offset, int customer) {
		connectToDatabase();
		int i = 0;
		String link = Database.getCustLink(customer, connection);
		try {
			i = (int) offset;
			System.out.println(i);
		} catch (ClassCastException e) {
		}
		String arg = "?";
		if (path.equals("bookings")) {
			if (RESET) {
				//createDatabase(SQL_BOOK+OPT_BOOK);
				createDatabase("DELETE FROM bookings WHERE id=" + customer + "");
			} else {
				arg = "?createdAfter=" + offset + "&";
				i = 0;
			}
			int count = 0;
			SQLThread t = new SQLThread(this, path, customer);
			System.out.println(link + path + arg + "limit=500&offset=" + i);
			while ((int) getData(link + path + arg + "limit=500&offset=" + i, t) > 0) {
				i += 500;
				count += 500;
				System.out.println(i);
				if (count % 3500 == 0) {
					t.start();
					t = new SQLThread(this, path, customer);
				}
			}
			if (count % 3500 != 0) {
				t.start();
			}
		} else if (path.equals("locations")) {
			if (RESET ) {
				createDatabase(SQL_LOC + OPT_LOC);
				createDatabase(SQL_ADD + OPT_ADD);
			}
			List<JSONObject> total = new ArrayList<JSONObject>();
			List<JSONObject> temp;
			temp = (List<JSONObject>) getData(link + path + "?limit=500&offset=" + i, null);
			total.addAll(temp);
			temp = (List<JSONObject>) getData(link + path + "?type=terminals&limit=500&offset=" + i, null);
			total.addAll(temp);
			temp = (List<JSONObject>) getData(link + path + "?type=ports&limit=500&offset=" + i, null);
			total.addAll(temp);
			insertLocation(total, "loc");
			insertLocation(total, "add");
		} else if (path.equals("actions")) {
			if (RESET) {
				createDatabase("DELETE FROM st_actions WHERE id=" + customer);
				createDatabase("DELETE FROM actions WHERE id=" + customer);
			}
			
			SQLThread t = new SQLThread(this, path, customer);
			int counter = 0;
			while ((int) getData(link + path + "?limit=500&offset=" + i, t) > 0) {
				i += 500;
				counter += 1;
				System.out.println(i);
				if (counter % 7 == 0) {
					t.start();
					t = new SQLThread(this, path, customer);
				}
			}
			if (counter % 7 != 0) {
				t.start();
			}
		} else if(path.equals("linestops")) {
			System.out.println("create table");
			if (RESET) {
				createDatabase(SQL_LINE + OPT_LINE);
			}
			
			Long lower = (Long) offset;
			if (lower == 0) {
				lower = new Long(1500000000);
			}
			Date date = new Date();
			long time = date.getTime()/1000;
			List<JSONObject> temp;
			temp = (List<JSONObject>) getData(link + path + "?modality=BARGE&lowerBound=" + lower + "&upperBound=" + time, null);
			if (temp.size() != 1) {
				insertLinestops(temp);
			}
		}
	}
	
	public static String getCustLink(int customer, Connection c) {
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
	
	public static int getCustId(String customer, Connection c) {
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
	
	//Executes sql command.
	//var command is full SQL create query
	private void createDatabase(String command) {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(command);
			System.out.println("success");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			statement.close();
		} catch (Exception e) {
			System.out.println("error");
		}
	}
	
	//insertion of actions into SQL
	public void insertAction(List<String> data, int customer) {
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
		}
		
	}
	
	//insertion of locations in SQL
	private void insertLocation(List<JSONObject> data, String act) {
		String[] options = OPT_LOC.split(",");
		String[] addOptions = OPT_ADD.split(",");
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
				try {s.close();}catch(SQLException e){}
			}
		}
		System.out.println("finished");
		}
	
	//insertion of linestops in SQL
	private void insertLinestops(List<JSONObject> data) {
		String[] options = OPT_LINE.split(",");
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
				try {s.close();}catch(SQLException e){}
			}
		}
		System.out.println("finished Linestops");
	}
	
	//insertion of bookings in SQL
	public void insertDatabase(List<JSONObject> data, String command, String opts) {
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
				try {s.close();}catch(SQLException e){}
			}
		}
	}
	
	public String insertCustomer(String name, String link) {
		connectToDatabase();
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
			try {this.connection.close();} catch(SQLException e) {}
			return "WORKS";
		} catch (SQLException e) {
			try {this.connection.close();} catch(SQLException f) {}
			return e.toString();
		}
		
		
		
	}
	
	public static void main(String args[]) {
		Database d = new Database();
		d.connectToDatabase();
		d.update(true, "module4t2");
		try {d.connection.close();}catch(SQLException e) {}
		//d.createDatabase(SQL_CUST+PUT_CUST1+PUT_CUST2);
	}
	
	//initiates all updates.
	//locations get reset every time
	//bookings and linestops only get updated normally
	public void update(boolean RESET, String customer) {
		int i = 0;
		if (i > 0) {
			int custId = Database.getCustId(customer, connection);
			System.out.println("CUSTID : " + custId);
			updateBook(RESET, custId);
			updateLoc(custId);
			updateLines(RESET, custId);
			updateActions(true, custId);
		} else {
			System.out.println("Dummy Procedure to check if update works");
		}
		
	}
	
	public void update(boolean RESET, String customer, String B_L_A_S) {
		if (B_L_A_S == null) {
			update(RESET, customer);
		} else {
			int custId = Database.getCustId(customer, connection);
			String[] temp = B_L_A_S.split("_");
			if (temp.length == 4) {
				if (temp[0].equals("t")) {
					updateBook(RESET, custId);
				}
				if (temp[1].equals("t")) {
					updateLoc(custId);
				}
				if (temp[2].contentEquals("t")) {
					updateActions(RESET, custId);
				}
				if (temp[3].equals("t")) {
					updateLines(RESET, custId);
				}
			} else {
				System.out.println(B_L_A_S + " :: " + temp);
			}
		}
	}
	
	private void updateBook(boolean RESET, int customer) {
		if (!RESET) {
			connectToDatabase();
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
				makeTable("bookings", RESET, offBook/1000+1,  customer);
			} else {
				makeTable("bookings", RESET, 0, customer);
			}
		} else {
			makeTable("bookings", RESET, 0, customer);
		}
	}
	private void updateLoc(int customer) {
		makeTable("locations", true, 0, customer);
	}
	private void updateLines(boolean RESET, int customer) {
		if (!RESET) {
			String command = "SELECT MAX(sta) AS c FROM linestops";
			Timestamp x = null;
			try {
				connectToDatabase();
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
			
			makeTable("linestops", RESET, (x.getTime())/1000, customer);	
		} else {
			makeTable("linestops", RESET, new Long(0), customer);
		}
		
	}
	private void updateActions(boolean RESET, int customer) {
		makeTable("actions", RESET, 0, customer);
		Statistics s = new Statistics();
		s.connectToDatabase();
		s.parseActions(true, customer);
		try {s.connection.close();} catch (SQLException e) {}
	}
	//execute HTTP request and return either Array of JSON objects or String objects
	public static Object getData(String path, SQLThread t) {
		URL url;
		List<JSONObject> o = null;
		try {
			url = new URL(path);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", AUTH_CODE);
			int req = con.getResponseCode();
			if (req == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				if (t != null) {
					t.parse(response.toString());
					if (response.toString().length() <= 5) {
						return -1;
					} else {
						return 1;
					}
				}
				o = parse(response.toString());
			} else {
				System.err.println("error: connection failed\n" + con.getResponseMessage());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return o;
		
	}
	//JSON parser, creates list of entities
	public static List<JSONObject> parse(String json) {
		List<JSONObject> temp = new ArrayList<JSONObject>();
		String data = json;
		int i = 0;
		while (data.length() > 6){
			if (!data.substring(1,2).equals("{"))  {
				return null;
			}
			if (data.substring(i,i+1).equals("}")) {
				if (i == data.length()-2) {
					//System.out.println(data.substring(1,i+1));
					temp.add(new JSONObject(data.substring(1,i+1)));
					data = "";
					i = -1;
				} else if (data.substring(i+1,i+3).equals(",{")) {
					//System.out.println(data.substring(1,i+1));
					temp.add(new JSONObject(data.substring(1,i+1)));
					data = data.substring(0,1) + data.substring(i+2);
					i = -1;
				}
			} else {
				if (i == data.length()-2) {
					temp.add(new JSONObject(data.substring(1,i)));
					data = "";
				}
			}
			i += 1;
		} 
		return temp;
	}
	//JSON parser, creates list of entities for ACTIONS
	public static List<String> parseActions(String json) {
		List<String> temp = new ArrayList<String>();
		String data = new String(json);
		data.replace("`", "inch");
		int i = 0;
		int open = 0;
		while (data.length() > 6) {
			if (!data.substring(1,2).equals("{")) {
				return null;
			}
			if (data.substring(i,i+1).equals("}")) {
				//System.out.print("}" + open);
				open -= 1;
				if (i == data.length()-2) {
					//System.out.println(data.substring(1,i+1));
					temp.add(data.substring(1,i+1));
					data = "";
				} else if (data.substring(i+1,i+3).equals(",{") && open == 0) {
					//System.out.println(data.substring(1,i+1));
					temp.add(data.substring(1,i+1));
					data = data.substring(0,1) + data.substring(i+2);
					i = -1;
				}
			} else if (i == data.length() -2) { 
				temp.add(data.substring(1,i));
				data = "";
			} else if (data.substring(i,i+1).equals("{")) {
				//System.out.print("{"+ (open + 1));
				open += 1;
			} else if (data.substring(i,i+1).equals("'")) {
				data = data.substring(0,i) + data.substring(i+1);
			}
			i += 1;
		}
		try {Thread.sleep(1000);}catch(InterruptedException e) {}
		return temp;
	}
	
	private static final String SQL_CUST = "DROP TABLE IF EXISTS customers; CREATE SEQUENCE cust_seq; CREATE TABLE customers ("
			+ "id INT NOT NULL DEFAULT nextval('cust_seq'),"
			+ "name VARCHAR(50) PRIMARY KEY,"
			+ "link TEXT);"
			+ "ALTER SEQUENCE cust_seq OWNED BY customers.id;";
	private static final String PUT_CUST1 = "INSERT INTO customers (name,link) VALUES ('module4t1', 'https://module4t1-test.cofanostack.com/api/bigbrother/');";
	private static final String PUT_CUST2 = "INSERT INTO customers (name,link) VALUES ('module4t2', 'https://module4t2-test.cofanostack.com/api/bigbrother/');";
	private static final String SQL_BOOK = "DROP TABLE IF EXISTS bookings; CREATE TABLE bookings (";
	private static final String SQL_LOC = "DROP TABLE IF EXISTS locations; CREATE TABLE locations (";
	private static final String SQL_ADD = "DROP TABLE IF EXISTS address; CREATE TABLE address (";
	private static final String SQL_ACTIONS_JSON = "DROP TABLE IF EXISTS actions; CREATE TABLE actions (action json);";
	private static final String SQL_TASK = "DROP TABLE IF EXISTS tasks; CREATE TABLE tasks (";
	private static final String SQL_LINE = "DROP TABLE IF EXISTS linestops; CREATE TABLE linestops (";
	private static final String OPT_LINE = "linestopId INT,"
			+ "parentId INT,"
			+ "previousId INT,"
			+ "locationId INT,"
			+ "locationName VARCHAR(100),"
			+ "sta TIMESTAMP,"
			+ "std TIMESTAMP,"
			+ "modalityType VARCHAR(10));";
	public static final String OPT_BOOK = "dossierId INT NOT NULL,"
			+ "bookingId INT,"
			+ "orderState VARCHAR(10),"
			+ "bookingIdentifier VARCHAR(20),"
			+ "containerNumber VARCHAR(20),"
			+ "createdOn TIMESTAMP,"
			+ "createdBy VARCHAR(63),"
			+ "referenceDate TIMESTAMP,"
			+ "containerType VARCHAR(30),"
			+ "teu FLOAT(3),"
			+ "shippingCompany VARCHAR(63),"
			+ "shippingCompanyId INT,"
			+ "shippingCompanyScac VARCHAR(63),"
			+ "nettoWeight DECIMAL,"
			+ "brutoWeight DECIMAL,"
			+ "customer VARCHAR(63))";
	private static final String OPT_LOC = "locationId INT NOT NULL,"
			+ "type VARCHAR(20),"
			+ "name VARCHAR(100),"
			+ "longitude REAL,"
			+ "latitude REAL,"
			+ "color VARCHAR(50),"
			+ "globalSearch BOOLEAN,"
			+ "aliases VARCHAR(50),"
			+ "terminalCode VARCHAR(100),"
			+ "unlo VARCHAR(50));";
	private static final String OPT_ADD = "locationId INT NOT NULL,"
			+ "street VARCHAR(64),"
			+ "number VARCHAR(15),"
			+ "postfix VARCHAR(50),"
			+ "postalCode VARCHAR(50),"
			+ "city VARCHAR(20),"
			+ "country VARCHAR(20));";
	public static final String OPT_ACT = "bookingId INT,"
			+ "bookingIdentifier VARCHAR(10),"
			+ "actionId INT,"
			+ "actionType VARCHAR(20),"
			+ "deletedOn TIMESTAMP,"
			+ "index INT,"
			+ "status VARCHAR(20),"
			+ "sta TIMESTAMP,"
			+ "std TIMESTAMP,"
			+ "startLocationId INT,"
			+ "startLocationType VARCHAR(20),"
			+ "startLocationName VARCHAR(63),"
			+ "startLocationAddress VARCHAR(63),"
			+ "startLocationCity VARCHAR(63),"
			+ "endLocationId INT,"
			+ "endLocationType VARCHAR(63),"
			+ "endLocationName VARCHAR(63),"
			+ "endLocationAddress VARCHAR(63),"
			+ "endLocationCity VARCHAR(63),"
			+ "loadLinestopId INT ,"
			+ "unloadLinestopId INT,"
			+ "shipName VARCHAR(63),"
			+ "locationId INT,"
			+ "locationType VARCHAR(63),"
			+ "locationName VARCHAR(63),"
			+ "locationAddress VARCHAR(63),"
			+ "locationCity VARCHAR(63),"
			+ "gateIn VARCHAR(63),"
			+ "gateOut VARCHAR(63),"
			+ "applicationTerminal VARCHAR(10));";
	private static final String OPT_TASK = "actionId INT,"
			+ "id INT,"
			+ "name ,"
			+ "taskClass ,"
			+ "taskFunction ,"
			+ "makeEmpty VARCHAR(5),"
			+ "makeFull VARCHAR(5),"
			+ "inPlanning VARCHAR(5),"
			+ "removeRemainingProducts VARCHAR(5),"
			+ "handlings INT,"
			+ "color VARCHAR(20),"
			+ "blockingType VARCHAR(20),"
			+ "blocking VARCHAR(5),"
			+ "taskState VARCHAR(20),"
			+ "startDate TIMESTAMP,"
			+ "finishDate TIMESTAMP,"
			+ "listPosition INT,"
			+ "externalParty VARCHAR(63),"
			+ "externalReference VARCHAR(63),"
			+ "";
}
