package nl.utwente.di.SQL;

import java.sql.*;
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
	private static final String GET_URL = "https://module4t2-test.cofanostack.com/api/bigbrother/bookings?limit=500&offset=";
	private static final String AUTH_CODE = "Basic QmlnQnJvdGhlcjpob3R0ZW50b3R0ZW50ZW50ZW50ZW50b29uc3RlbGxpbmc=";
	
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
	
	private void insertDatabase(List<JSONObject> data) {
		String command = "INSERT INTO bookings VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String[] options = OPT_BOOK.split(",");
		for (JSONObject item:data) {
			try {
				PreparedStatement s = connection.prepareStatement(command);
				for (int i = 1; i <= options.length; i ++) {
					String c = options[i-1].split(" ")[0];
					try {
						s.setString(i, item.getString(c));
					} catch (JSONException z) {
						try {
							s.setInt(i, (int) item.getInt(c));
						} catch (JSONException e) {
							//System.out.println("error: " + c + " is null");
							s.setObject(i, null);
						}
					}
				}
				s.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("finished");
	}
	
	public static void main(String args[]) {
		Database d = new Database();
		d.connectToDatabase();
		//d.createDatabase(SQL_BOOK+OPT_BOOK);
		List<JSONObject> temp;
//		int i = 73910;
//		while ((temp = d.getData(GET_URL+i)).size() != 0) {
//			d.insertDatabase(temp);
//			i += 500;
//			System.out.println(i);
//		}
		d.getData(GET_URL+83410);
		
		try {
			d.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private List<JSONObject> getData(String path) {
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
				System.out.println(o.size());
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
	private List<JSONObject> parse(String json) {
		System.out.print("parsing ");
		List<JSONObject> temp = new ArrayList<JSONObject>();
		String data = json;
		int i = 0;
		while (data.length() > 6){
			if (data.substring(i,i+1).equals("}")) {
				if (i == data.length()-2) {
					//System.out.println(data.substring(1,i+1));
					System.out.print("/");
					temp.add(new JSONObject(data.substring(1,i+1)));
					data = "";
				} else if (data.substring(i+1,i+3).equals(",{")) {
					//System.out.println(data.substring(1,i+1));
					System.out.print("/");
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
	private static final String OPT_BOOK = "dossierId INT NOT NULL,"
			+ "bookingId INT,"
			+ "orderState VARCHAR(10),"
			+ "bookingIdentifier VARCHAR(20),"
			+ "containerNumber VARCHAR(20),"
			+ "createdOn INT,"
			+ "createdBy VARCHAR(63),"
			+ "referenceDate INT,"
			+ "containerType VARCHAR(30),"
			+ "teu FLOAT(3),"
			+ "shippingCompany VARCHAR(63),"
			+ "shippingCompanyId INT,"
			+ "shippingCompanyScac VARCHAR(63),"
			+ "nettoWeight DECIMAL,"
			+ "brutoWeight DECIMAL,"
			+ "customer VARCHAR(63))";
	private static final String OPT_ACTIONS = "bookingId INT NOT NULL,"
			+ "bookingIdentifier VARCHAR(20),"
			+ "actionId INT,"
			+ "actionType VARCHAR(63),"
			+ "deleteOn VARCHAR(63),"
			+ "index INT,"
			+ "status VARCHAR(63),"
			+ "sta VARCHAR(63),"
			+ "std VARCHAR(63),"
			+ "startLocationId INT,"
			+ "startLocationType VARCHAR(63),"
			+ "startLocationName VARCHAR(63),"
			+ "startLocationAddress VARCHAR(63),"
			+ "startLocationCity INT,"
			+ "endLocationId VARCHAR(63),"
			+ "endLocationType VARCHAR(63),"
			+ "endLocationName VARCHAR(63),"
			+ "endocationAddress VARCHAR(63),"
			+ "endLocationCity VARCHAR(63),"
			+ "loadLinestopId INT,"
			+ "unloadLinestopId INT,"
			+ "shipName VARCHAR(63),"
			+ "locationId INT,"
			+ "locationType VARCHAR(63),"
			+ "locationName VARCHAR(63),"
			+ "locationAddress VARCHAR(63),"
			+ "locationCity VARCHAR(63),"
			+ "gateIn VARCHAR(63),"
			+ "gateOut VARCHAR(63),"
			+ "applicationTerminal VARCHAR(6))";
}
