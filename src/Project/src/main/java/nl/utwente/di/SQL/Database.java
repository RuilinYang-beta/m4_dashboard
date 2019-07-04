package nl.utwente.di.SQL;

import DAO.*;

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
	private static final String GET_URL = "https://module4t2-test.cofanostack.com/api/bigbrother/";
	private static final String AUTH_CODE = "Basic QmlnQnJvdGhlcjpob3R0ZW50b3R0ZW50ZW50ZW50ZW50b29uc3RlbGxpbmc=";

	//Instantiates HTTP request and SQL insertion for the database
	//requires 4 variables:
	//path=table, so either "locations", "bookings", "linestops", or "actions"
	//reset=reset the table, ergo creates a new one
	//offset=start value, if you dont reset table, we dont need all new info
	//offset can be either int or Long
	//customer=customer id to keep table unique
	public static void makeTable(String path, boolean RESET, Object offset, int customer) {
		int i = 0;
		String link = DAOcustomer.getCustLink(customer);
		try {
			i = (int) offset;
			System.out.println(i);
		} catch (ClassCastException e) {
		}
		String arg = "?";
		if (path.equals("bookings")) {
			if (RESET) {
				//createDatabase(SQL_BOOK+OPT_BOOK);
				DAOgeneral.execute("DELETE FROM bookings WHERE id=" + customer);
			} else {
				arg = "?createdAfter=" + offset + "&";
				i = 0;
			}
			int count = 0;
			SQLThread t = new SQLThread(path, customer);
			while ((int) getData(link + path + arg + "limit=500&offset=" + i, t) > 0) {
				i += 500;
				count += 500;
				System.out.println(i);
				if (count % 3500 == 0) {
					t.start();
					t = new SQLThread(path, customer);
				}
			}
			if (count % 3500 != 0) {
				t.start();
			}
		} else if (path.equals("locations")) {
			if (RESET ) {
				DAOgeneral.execute(SQL_LOC + OPT_LOC);
				DAOgeneral.execute(SQL_ADD + OPT_ADD);
			}
			List<JSONObject> total = new ArrayList<JSONObject>();
			List<JSONObject> temp;
			temp = (List<JSONObject>) getData(link + path + "?limit=500&offset=" + i, null);
			total.addAll(temp);
			temp = (List<JSONObject>) getData(link + path + "?type=terminals&limit=500&offset=" + i, null);
			total.addAll(temp);
			temp = (List<JSONObject>) getData(link + path + "?type=ports&limit=500&offset=" + i, null);
			total.addAll(temp);
			DAOinsert.insertLocation(total, "loc");
			DAOinsert.insertLocation(total, "add");
		} else if (path.equals("actions")) {
			if (RESET) {
				DAOgeneral.execute("DELETE FROM st_actions WHERE id=" + customer);
				DAOgeneral.execute("DELETE FROM actions WHERE id=" + customer);
			}
			
			SQLThread t = new SQLThread(path, customer);
			
			int counter = 0;
			while ((int) getData(link + path + "?limit=500&offset=" + i, t) > 0) {
				i += 500;
				counter += 1;
				System.out.println(i);
				if (counter % 7 == 0) {
					t.start();
					t = new SQLThread(path, customer);
				}
			}
			if (counter % 7 != 0) {
				t.start();
			}
		} else if(path.equals("linestops")) {
			System.out.println("create table");
			if (RESET) {
				//createDatabase("DELETE FROM linestops WHERE id=" + customer);
			}
			
			Long lower = (Long) offset;
			if (lower == 0) {
				lower = new Long(1300000000);
			}
			Date date = new Date();
			//long time = date.getTime()/1000-500000;
			long time = new Long(1500000000);
			List<JSONObject> temp;
			System.out.println(link + path + "?modality=BARGE&lowerBound=" + lower + "&upperBound=" + time);
			temp = (List<JSONObject>) getData(link + path + "?modality=BARGE&lowerBound=" + lower + "&upperBound=" + time, null);
			if (temp.size() != 1) {
				DAOinsert.insertLinestops(temp);
			}
		}
	}
	
	/**-----------------------------------------**/
	public static void main(String args[]) {

	}
	
	//initiates all updates.
	//locations get reset every time
	//bookings and linestops only get updated normally
	public static void update(boolean RESET, String customer) {
		int i = 0;
		if (i > 0) {
			int custId = DAOcustomer.getCustId(customer);
			DAOupdate.updateBook(RESET, custId);
			DAOupdate.updateLoc(custId);
			DAOupdate.updateLines(RESET, custId);
			DAOupdate.updateActions(true, custId);
		} else {
			System.out.println("Dummy Procedure to check if update works");
		}
		
	}
	
	public static void update(boolean RESET, String customer, String B_L_A_S) {
		if (B_L_A_S == null) {
			update(RESET, customer);
		} else {
			int custId = DAOcustomer.getCustId(customer);
			String[] temp = B_L_A_S.split("_");
			if (temp.length == 4) {
				if (temp[0].equals("t")) {
					DAOupdate.updateBook(RESET, custId);
				}
				if (temp[1].equals("t")) {
					DAOupdate.updateLoc(custId);
				}
				if (temp[2].contentEquals("t")) {
					DAOupdate.updateActions(RESET, custId);
				}
				if (temp[3].equals("t")) {
					DAOupdate.updateLines(RESET, custId);
				}
			} else {
				System.out.println(B_L_A_S + " :: " + temp);
			}
		}
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
	public static final String OPT_LINE = "linestopId INT,"
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
	public static final String OPT_LOC = "locationId INT NOT NULL,"
			+ "type VARCHAR(20),"
			+ "name VARCHAR(100),"
			+ "longitude REAL,"
			+ "latitude REAL,"
			+ "color VARCHAR(50),"
			+ "globalSearch BOOLEAN,"
			+ "aliases VARCHAR(50),"
			+ "terminalCode VARCHAR(100),"
			+ "unlo VARCHAR(50));";
	public static final String OPT_ADD = "locationId INT NOT NULL,"
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
