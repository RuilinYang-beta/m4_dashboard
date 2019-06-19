package nl.utwente.di.SQL;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import org.json.*;

@Path("/sql")
public class Server{
		
	@GET
	@Path("/count")
	@Produces(MediaType.TEXT_HTML)
	public String getCount() {
		Statistics a = new Statistics();
		a.connectToDatabase();
		String message = a.getCount()+ "";
		return message;
	}
	
	@GET
	@Path("/year")
	@Produces(MediaType.TEXT_HTML)
	public String getYears() {
		Statistics a = new Statistics();
		a.connectToDatabase();
		ResultSet x = a.execute("SELECT date, counter FROM st_book WHERE date <> '_' ORDER BY date DESC;");
		return parseToStringarray(x);
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
	
	@GET
	@Path("/select")
	public String bookingsPerDate(@QueryParam("fromD") Long dateLow,
									@QueryParam("toD") Long dateHigh, 
									@QueryParam("dosId") int dosId,
									@QueryParam("ordState") String ordState,
									@QueryParam("teu") int teu,
									@QueryParam("shipComp") String company,
									@QueryParam("shipCompId") int compId,
									@QueryParam("shipCompScac") String compScac,
									@QueryParam("goal") String goal) {
		Object[] inserts = new Object[]{null, null, null, null, null, null, null, null};
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
		if (dosId != 0) {
			command += " AND dossierId = ?";
			inserts[2] = dosId;
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
		return getValue(command, inserts, goal);	
	}
	
	private String getValue(String command, Object[] inserts, String goal) {
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
				stm.setObject(col, (int) inserts[2]);
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
			}
			ResultSet x = stm.executeQuery();
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
}
