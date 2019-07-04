package resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import org.json.*;
import Utils.SQLUtils;
import nl.utwente.di.SQL.Database;
import nl.utwente.di.SQL.Statistics;
import DAO.*;
@Path("/sql")
public class Server{
	public static final String LINK = "http://localhost:8080";
	
	/**
	 * Function to return the data used in geo map.
	 */
	@GET
	@Path("/linestops")
	public String getLinestops() { 
		ResultSet rs = DAOgeneral.execute("SELECT linestopid, locationid, DATE(sta) AS sta_date, DATE(std) AS std_date FROM linestops"); 
		return parseJSON(rs).toString();
	}
	
	/**
	 * Function to return data used in geo map's slide bar.
	 */
	@GET
	@Path("/uniquesta")
	public String getLinestopsUniqueStaDate() { 
//		ResultSet rs = stat.execute("SELECT DISTINCT sta_date FROM (SELECT DATE(sta) AS sta_date FROM linestops ORDER BY sta_date) AS sub"); 
		ResultSet rs = DAOgeneral.execute("SELECT DISTINCT(DATE(sta)) AS sta_date FROM linestops ORDER BY sta_date"); 
		
		return SQLUtils.glueColumnIntoString(rs);
	}
	
	@GET
	@Path("/autoupdate")
	public String setTimer(@QueryParam("time") int time) {
		int t = 0;
		if (time <= 0) {
			t = 360; //1 hr
		}
		
		autoUpdate aU = new autoUpdate(this, t+(time*time)/time);
		aU.start();
		return "started";
	}
	
	@GET
	@Path("/auth")
	public boolean checkEmployee(@QueryParam("mail") String mail) {
		return DAOemployee.checkEmployee(mail);
	}
	@POST
	@Path("/auth")
	public String addEmployee(@QueryParam("name") String name,
							@QueryParam("id") int id, 
							@QueryParam("mail") String mail) {
		return DAOemployee.addEmployee(name, id, mail);
	}
	@DELETE
	@Path("/auth")
	public String deleteEmployee(@QueryParam("name") String name,
								@QueryParam("id") int id,
								@QueryParam("mail") String mail) {
		if (name != null) {
			if (id != 0) {
				System.out.println("id given");
				return DAOemployee.deleteEmployee(name, id);
			} else if (mail != null) {
				System.out.println("mail given");
				return DAOemployee.deleteEmployee(name, mail);
			} else {
				System.out.println("error: nothing given");
				return "give id or mail plix";
			}
		} else {
			return "give name plix";
		}
	}
	

		
	@GET
	@Path("/count")
	@Produces(MediaType.TEXT_HTML)
	public String getCount() {
		String message = Statistics.getCount()+ "";
		return message;
	}
	

	@POST
	@Path("/update")
	public String addEnvironment(@QueryParam("name") String name,
									@QueryParam("link") String link,
									@QueryParam("B_L_A_S") String bools) { //b=bookings, L=locations, A=actions, S=linestops t for true
									//example: bookings and actions: t_f_t_f   every option: t_t_t_t 
		DAOinsert.insertCustomer(name, link);
		Database.update(true, name, bools);
		return "";
	}

	
	//update database values
	@GET
	@Path("/update")
	public String updateDatabase() {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT name, id FROM customers");
			System.out.println("pulling customers");
			while (rs.next()) {
				System.out.println(rs.getString(1) + " " + rs.getInt(2));
				Database.update(false, rs.getString("name"));
			}
		} catch (SQLException e) {
			System.err.println("error getting stuff");
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				
			}
		}
		return "";
	}

	private class autoUpdate extends Thread {
		private Server s;
		private int frequency;
		
		//give frequency in seconds between updates
		public autoUpdate(Server s, int frequency) {
			this.frequency = frequency;
			this.s = s; 
		}
		
		public void run() {
			int i = 0;
			
			while (true) {
				while (i < frequency) {
					i += 1;
					try{Thread.sleep(1000);}catch(InterruptedException e) { System.out.println("error thread timer");}
				}
				s.updateDatabase();
				i = 0;
			}
		}
	}

	public static String parseToStringarray(ResultSet set) {
		StringBuffer res1 = new StringBuffer();
		StringBuffer res2 = new StringBuffer();
		StringBuffer res3 = new StringBuffer();
		String res = "";
		ResultSetMetaData rsmd;
		int col = -1;
		try {
			rsmd = set.getMetaData();
			col = rsmd.getColumnCount();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			while (set.next()) {
				res1.append(set.getObject(1)+";");
				res2.append(set.getObject(2)+";"); 
			
				if(col == 3) {
					res3.append(set.getObject(3)+";");
				}
				
			}
			if (res1.length() != 0 && res2.length() != 0) {
				res1.setLength(res1.length()-1);
				res2.setLength(res2.length()-1);
				if(col == 3 && res3.length() != 0) {
					res3.setLength(res3.length()-1);
				}
				
				res = res1.toString() + "|" + res2.toString();
				if(col == 3) {
					res += "|" + res3.toString();
				}
			} else {
				return "|";
			}
			
			
		} catch (SQLException e) {

		}
		
		return res;
	}
	
	public String resultSetToStringArray(ResultSet rs) {
		String resultString = "";
		try {
			while(rs.next()) {
				if(rs.isLast()) {
					resultString += rs.getString(1);
				} else {
					resultString += rs.getString(1) + ";";
				}
				
			}
		} catch (SQLException e) {
			System.out.println("Sorry, it doesn't work");
			e.printStackTrace();
		}
		return resultString;
	}
	@GET
	@Path("/getinfo")
	public String getInfo(@QueryParam("infoType") String infoType) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		PreparedStatement stm = null;
		ResultSet x = null;
		
		if (infoType.equals("customerNames")) {
			try {
				stm = connection.prepareStatement("SELECT DISTINCT customer FROM bookings WHERE customer <> 'null' AND customer <> '' ORDER BY customer");
				x = stm.executeQuery();
			} catch (SQLException e) {
			}
		} else if (infoType.equals("shippingCompanyNames")) {
			try {
				stm = connection.prepareStatement("SELECT DISTINCT shippingCompany FROM bookings WHERE shippingCompany <> 'null' AND shippingCompany <> '' ORDER BY shippingCompany");
				x = stm.executeQuery();
			} catch (SQLException e) {
			}
		} else if (infoType.equals("customerId")) {
			try {
				stm = connection.prepareStatement("SELECT DISTINCT id FROM bookings ORDER BY id");
				x = stm.executeQuery();
			} catch (SQLException e) {
			}
		} else {
			return "abc";
		}
		try {connection.close();}catch(SQLException e) {}
		return resultSetToStringArray(x);
	}
	
	@GET
	@Path("/select")
	public String bookingsPerDate(@QueryParam("fromD") Long dateLow,
									@QueryParam("toD") Long dateHigh, 
									@QueryParam("ordState") String ordState,
									@QueryParam("customer") String customer,
									@QueryParam("teu") int teu,
									@QueryParam("shipComp") String company,
									@QueryParam("shipCompId") int compId,
									@QueryParam("shipCompScac") String compScac,
									@QueryParam("goal") String goal,
									@QueryParam("table") String  table,
									@QueryParam("customerId") int custId
									) {
		Object[] inserts = new Object[]{null, null, null, null, null, null, null, null, null};
		String command = "WHERE 0=0 AND orderState <> 'INPLANNING' AND orderState <> 'INVOICE'" + 
				"AND orderState <> 'DRAFT' AND orderState <> 'INVOICED' AND orderState <> 'INVOICABLE' AND orderState <> 'CANCELLED'";
		if (dateLow != null) {
			command += " AND createdOn > ?";
			inserts[0] = dateLow;
		}
		if (dateHigh != null) {
			command += " AND createdOn < ?";
			inserts[1] = dateHigh;
		}
		if (customer != null) {
			command += " AND customer = ?";
			inserts[2] = customer;
		}
		if (ordState != null) {
			command += " AND orderState = ?";
			inserts[3] = ordState;
		}
		if (teu != 0) {
			command += " AND teu = ?";
			inserts[4] = teu;
		}
		if (company != null) {
			command += " AND shippingCompany = ?";
			inserts[5] = company;
		}
		if (compId != 0) {
			command += " AND shippingCompanyId = ?";
			inserts[6] = compId;
		}
		if (compScac != null) {
			command += " AND shippingCompanyScac = ?";
			inserts[7] = compScac;
		}
		if (custId != 0) {
			command += " AND id = ?";
			inserts[8] = custId;
		}
		if (table != null) {
			if(table.equals("true")) {
				return getValue(command, inserts, goal, true);
			}
		}
		return getValue(command, inserts, goal, false);	
	}
	
	private String getValue(String command, Object[] inserts, String goal, boolean query) {
		Connection connection = null;
		connection = DAOgeneral.connectToDatabase(connection);
		int i = -1;
		try {
			PreparedStatement stm = null;
			if (goal == null) {
				return -2 + "";
			} else if (((String) goal).equals("bookings")) {
				stm = connection.prepareStatement(SORT_MONTH + "COUNT(*) FROM bookings " + command + " GROUP BY m_y ORDER BY m_y;");
			} else if (((String) goal).equals("nettoWeight")) {
				stm = connection.prepareStatement(SORT_MONTH + "SUM(nettoWeight) FROM bookings " + command + " GROUP BY m_y ORDER BY m_y;");
			} else if (((String) goal).equals("brutoWeight")) { 
				stm = connection.prepareStatement(SORT_MONTH + "SUM(brutoWeight) FROM bookings " + command + " GROUP BY m_y ORDER BY m_y;");
			} else if (((String) goal).equals("topCustomerBook")){
				stm = connection.prepareStatement("SELECT customer AS custName, COUNT(bookingId) FROM bookings "  + command + topCustomer);		
			} else if (((String) goal).equals("topCustomerWeight")){
				stm = connection.prepareStatement("SELECT customer AS custName, SUM(brutoWeight) + SUM(nettoWeight) FROM bookings " + command + topCustomer);
			} else if (((String) goal).equals("2yAxis")){
				stm = connection.prepareStatement(SORT_MONTH + "COUNT(bookingId), SUM(brutoWeight) FROM bookings " + command + "AND createdOn IS NOT NULL GROUP BY m_y ORDER BY m_y;");
				
			}  else {
				return -2 + ""; 
			}
			int col = 1;
			if (inserts[0] != null) {
				stm.setObject(col,  new Timestamp((Long) inserts[0] * 1000));
				col += 1;
			} if (inserts[1] != null) {
				stm.setObject(col,  new Timestamp((Long) inserts[1] * 1000));
				col += 1;
			} if (inserts[2] != null) {
				stm.setObject(col, (String) inserts[2]);
				col += 1;
			} if (inserts[3] != null) {
				stm.setObject(col, (String) inserts[3]);
				col += 1;
			} if (inserts[4] != null) {
				stm.setObject(col, (int) inserts[4]);
				col += 1;
			} if (inserts[5] != null) {
				stm.setObject(col, (String) inserts[5]);
				col += 1;
			} if (inserts[6] != null) {
				stm.setObject(col, (int) inserts[6]);
				col += 1;
			} if (inserts[7] != null) {
				stm.setObject(col, (String) inserts[7]);
				col += 1;
			} if (inserts[8] != null) {
				stm.setObject(col, (int) inserts[8]);
				col += 1;
			}
			ResultSet x = stm.executeQuery();
			if (query) {
				int tot = 0;
				while (x.next()) {
					tot += x.getInt(2);
				}
				return tot + "";
			}
			return parseToStringarray(x);
		} catch (SQLException e) {
			System.out.println("SQL error");
			e.printStackTrace();
		} finally {
			try {connection.close();}catch(SQLException e) {}
		}
		return i + "";
	}
	
	public static JSONArray parseJSON(ResultSet rs) {
		JSONArray json = new JSONArray();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {
			  int numColumns = rsmd.getColumnCount();
			  JSONObject obj = new JSONObject();
			  for (int i=1; i<=numColumns; i++) {
			    String column_name = rsmd.getColumnName(i);
			    obj.put(column_name, rs.getObject(column_name));
			  }
			  json.put(obj);
			}
		} catch (SQLException sqle) {
			System.out.println("ERROR");
		}
		return json;
	}
	private static final String SORT_MONTH = "SELECT CONCAT(cast(EXTRACT(year FROM createdOn) AS VARCHAR(4))"
			+ ",'_', RIGHT(CONCAT('0',cast(EXTRACT(month FROM createdOn) AS VARCHAR(2))), 2)) AS m_y, ";
	private static final String topCustomer =  " AND brutoWeight IS NOT NULL AND customer IS NOT NULL GROUP BY custName ORDER BY (SUM(brutoWeight) + COUNT(bookingId)*1000) DESC LIMIT 10";
}