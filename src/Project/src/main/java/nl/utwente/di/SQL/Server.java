package nl.utwente.di.SQL;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import org.json.*;

@Path("/sql")
public class Server{
	
	public static final String LINK = "http://localhost:8080";
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
		Statistics s = new Statistics();
		return s.checkEmployee(mail);
	}
	@POST
	@Path("/auth")
	public String addEmployee(@QueryParam("name") String name,
							@QueryParam("id") int id, 
							@QueryParam("mail") String mail) {
		Statistics s = new Statistics();
		return s.addEmployee(name, id, mail);
	}
	@DELETE
	@Path("/auth")
	public String deleteEmployee(@QueryParam("name") String name,
								@QueryParam("id") int id,
								@QueryParam("mail") String mail) {
		Statistics s = new Statistics();
		if (name != null) {
			if (id != 0) {
				System.out.println("id given");
				return s.deleteEmployee(name, id);
			} else if (mail != null) {
				System.out.println("mail given");
				return s.deleteEmployee(name, mail);
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
		Statistics a = new Statistics();
		a.connectToDatabase();
		String message = a.getCount()+ "";
		return message;
	}
	

	@POST
	@Path("/update")
	public String addEnvironment(@QueryParam("name") String name,
									@QueryParam("link") String link,
									@QueryParam("B_L_A_S") String bools) { //b=bookings, L=locations, A=actions, S=linestops t for true
									//example: bookings and actions: t_f_t_f   every option: t_t_t_t 
		Database d = new Database();
		d.insertCustomer(name, link);
		d.update(true, name, bools);
		return "";
	}

	
	//update database values
		@GET
		@Path("/update")
		public String updateDatabase() {
			Database d = new Database();
			d.connectToDatabase();
			try {
				Statement s = d.connection.createStatement();
				ResultSet rs = s.executeQuery("SELECT name, id FROM customers");
				System.out.println("pulling customers");
				while (rs.next()) {
					System.out.println(rs.getString(1) + " " + rs.getInt(2));
					d.update(false, rs.getString("name"));
				}
			} catch (SQLException e) {
				System.err.println("error getting stuff");
				e.printStackTrace();
			} finally {
				try {
					d.connection.close();
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
	
		String res = "";
		try {
			while (set.next()) {
			
					res1.append(set.getObject(1)+";");
					res2.append(set.getObject(2)+";"); 
				
			
			}
			res1.setLength(res1.length()-1);
			res2.setLength(res2.length()-1);
		
			res = res1.toString() + "|" + res2.toString();
		} catch (SQLException e) {

		}
		return res;
	}
	
//	public static String parseToStringarray(ResultSet set) {
//		StringBuffer res1 = new StringBuffer();
//		StringBuffer res2 = new StringBuffer();
//		StringBuffer res3 = new StringBuffer();
//		String res = "";
//		ResultSetMetaData rsmd;
//		int col = -1;
//		try {
//			rsmd = set.getMetaData();
//			col = rsmd.getColumnCount();
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//		try {
//			while (set.next()) {
//				res1.append(set.getObject(1)+";");
//				res2.append(set.getObject(2)+";"); 
//			
//				if(col == 3) {
//					res3.append(set.getObject(3)+";");
//				}
//				
//			}
//			res1.setLength(res1.length()-1);
//			res2.setLength(res2.length()-1);
//			if(col == 3) {
//				res3.setLength(res3.length()-1);
//			}
//			
//			res = res1.toString() + "|" + res2.toString();
//			if(col == 3) {
//				res += "|" + res3.toString();
//			}
//			
//		} catch (SQLException e) {
//
//		}
//		return res;
//	}
	
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
		Statistics a = new Statistics();
		a.connectToDatabase();
		int i = -1;
		try {
			PreparedStatement stm = null;
			if (goal == null) {
				return -2 + "";
			} else if (((String) goal).equals("bookings")) {
				stm = a.connection.prepareStatement(SORT_MONTH + "COUNT(*) FROM bookings " + command + " GROUP BY m_y ORDER BY m_y;");
			} else if (((String) goal).equals("nettoWeight")) {
				stm = a.connection.prepareStatement(SORT_MONTH + "SUM(nettoWeight) FROM bookings " + command + " GROUP BY m_y ORDER BY m_y;");
			} else if (((String) goal).equals("brutoWeight")) { 
				stm = a.connection.prepareStatement(SORT_MONTH + "SUM(brutoWeight) FROM bookings " + command + " GROUP BY m_y ORDER BY m_y;");
			} else if (((String) goal).equals("topCustomerWeight")){
				stm = a.connection.prepareStatement("SELECT customer AS custName, COUNT(bookingId)" + topCustomer);		
			} else if (((String) goal).equals("topCustomerBook")){
				stm = a.connection.prepareStatement("SELECT customer AS custName, SUM(brutoWeight)" + topCustomer);
				
			} else {
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
	private static final String topCustomer = " FROM bookings "
			+ "WHERE 0=0 AND orderState <> 'INPLANNING' AND orderState <> 'INVOICE'" +  
			"AND orderState <> 'DRAFT' AND orderState <> 'INVOICED' AND orderState <> 'INVOICABLE' AND orderState <> 'CANCELLED' "
			+ "AND brutoWeight IS NOT NULL AND customer IS NOT NULL GROUP BY custName ORDER BY (SUM(brutoWeight) + COUNT(bookingId)*1000) DESC LIMIT 10";
}
