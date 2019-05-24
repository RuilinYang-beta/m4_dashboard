package nl.utwente.di.SQL;

import java.sql.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;

import javassist.expr.NewArray;

import org.json.JSONException;

public class Database {
	public Connection connection;
	private static final String GET_URL = "https://module4t2-test.cofanostack.com/api/bigbrother/";
	private static final String AUTH_CODE = "Basic QmlnQnJvdGhlcjpob3R0ZW50b3R0ZW50ZW50ZW50ZW50b29uc3RlbGxpbmc=";
	public Database() {
		connectToDatabase();
	}
	
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
	
	private void makeTable(String path, boolean RESET, int offset) {
		connectToDatabase();
		int i = offset;
		String command = "";
		String opts = "";
		if (path.equals("bookings")) {
			if (RESET) {
				createDatabase(SQL_BOOK+OPT_BOOK);
			}
			command = "INSERT INTO bookings VALUES(";
			opts = OPT_BOOK;
			List<JSONObject> total = new ArrayList<JSONObject>();
			List<JSONObject> temp;
			while ((temp = getData(GET_URL + path + "?limit=500&offset=" + i)).size() > 0) {
				total.addAll(temp);
				i += 500;
				System.out.println(i);
				if (total.size() % 10000 == 0) {
					insertDatabase(total, command, opts);
					total = new ArrayList<JSONObject>();
				}
			}
			insertDatabase(total, command, opts);
		} else if (path.equals("locations")) {
			if (RESET ) {
				createDatabase(SQL_LOC + OPT_LOC);
				createDatabase(SQL_ADD + OPT_ADD);
			}
			List<JSONObject> total = new ArrayList<JSONObject>();
			List<JSONObject> temp;
			temp = getData(GET_URL + path + "?limit=500&offset=" + i);
			total.addAll(temp);
			i += 500;
			System.out.println(i);
			insertLocation(total);
		}
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
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
	
	private void insertLocation(List<JSONObject> data) {
		String[] options = OPT_LOC.split(",");
		String[] addOptions = OPT_ADD.split(",");
		StringBuffer execute = new StringBuffer();
		PreparedStatement s = null;
		for (JSONObject item:data) {
			execute.append("INSERT INTO locations VALUES(");
			for (int i = 1; i <= options.length; i ++) {
				String c = options[i-1].split(" ")[0];
				try {
					if (item.getString(c).length() == 0) {
						execute.append("null" + ",");
					} else {
						execute.append("'" + item.getString(c) + "'" + ",");
					}
				} catch (JSONException z) {
					try {
						execute.append(item.getLong(c) + ",");
					} catch (JSONException e) {
						execute.append("null" + ",");
					}
				}
			}
			execute.setLength(execute.length()-1);
			execute.append(");");
			
			execute.append("INSERT INTO address VALUES(");
			execute.append(item.getInt("locationId") + ",");
			for (int i = 2; i <= addOptions.length; i ++) {
				String c = addOptions[i-1].split(" ")[0];
				try {
					if (item.getJSONObject("address").getString(c).length() == 0) {
						execute.append("null" + ",");
					} else {
						execute.append("'" + item.getJSONObject("address").getString(c) + "'" + ",");
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
	
	private void insertDatabase(List<JSONObject> data, String command, String opts) {
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
	
	public static void main(String args[]) {
		Database d = new Database();
		d.update();
		//d.makeTable("bookings", true, 0);
	}
	
	public void update() {
		updateBook();
		updateLoc();
	}
	private void updateBook() {
		connectToDatabase();
		int offBook = 0;
		String command = "SELECT COUNT(DISTINCT bookingId) AS c FROM bookings;";
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery(command);
			while (rs.next()) {
				offBook = rs.getInt("c");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			connection.close();
		} catch (SQLException e) {
			
		}
		makeTable("bookings", false, offBook);
	}
	private void updateLoc() {
		makeTable("locations", true, 0);
	}
	
	public static List<JSONObject> getData(String path) {
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
			if (data.substring(i,i+1).equals("}")) {
				if (i == data.length()-2) {
					//System.out.println(data.substring(1,i+1));
					temp.add(new JSONObject(data.substring(1,i+1)));
					data = "";
				} else if (data.substring(i+1,i+3).equals(",{")) {
					//System.out.println(data.substring(1,i+1));
					temp.add(new JSONObject(data.substring(1,i+1)));
					data = data.substring(0,1) + data.substring(i+2);
					i = -1;
				}
			}
			i += 1;
		}
		return temp;
	}
	
	private static final String SQL_BOOK = "DROP TABLE IF EXISTS bookings; CREATE TABLE bookings (";
	private static final String SQL_LOC = "DROP TABLE IF EXISTS locations; CREATE TABLE locations (";
	private static final String SQL_ADD = "DROP TABLE IF EXISTS address; CREATE TABLE address (";
	private static final String OPT_BOOK = "dossierId INT NOT NULL,"
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
			+ "type VARCHAR(50),"
			+ "name VARCHAR(50),"
			+ "longitude REAL,"
			+ "latitude REAL,"
			+ "color VARCHAR(50),"
			+ "globalSearch BOOLEAN,"
			+ "aliases VARCHAR(50),"
			+ "terminalCode VARCHAR(50),"
			+ "unlo VARCHAR(50));";
	private static final String OPT_ADD = "locationId INT NOT NULL,"
			+ "street VARCHAR(64),"
			+ "number INT,"
			+ "postfix VARCHAR(10),"
			+ "postalCode VARCHAR(10),"
			+ "city VARCHAR(20),"
			+ "country VARCHAR(20));";
}
