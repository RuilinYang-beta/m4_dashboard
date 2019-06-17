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
		ResultSet x = a.execute("SELECT date, counter FROM st_book WHERE date <> '_' ORDER BY date DESC LIMIT 10;");
		return parseToStringarray(x);
	}
	
	public static String parseToStringarray(ResultSet set) {
		StringBuffer res1 = new StringBuffer();
		StringBuffer res2 = new StringBuffer();
		String res = "";
		try {
			while (set.next()) {
				res1.append(set.getString("date")+";");
				res2.append(set.getInt("counter")+";"); 
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
									@QueryParam("ordState") String OrdState,
									@QueryParam("fromN") int nettoLow,
									@QueryParam("toN") int nettoHigh,
									@QueryParam("fromB") int bruttoLow,
									@QueryParam("toB") int bruttoHigh,
									@QueryParam("teu") int teu,
									@QueryParam("shipComp") String company,
									@QueryParam("shipCompId") int compId,
									@QueryParam("shipCompScac") String compScac) {
		Statistics a = new Statistics();
		a.connectToDatabase();
		int i = -1;
		boolean[] inserts = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};
		try {
			String command = "WHERE 0=0 AND orderState <> 'INPLANNING' AND orderState <> 'INVOICE'" + 
					"AND orderState <> 'DRAFT' AND orderState <> 'INVOICED' AND orderState <> 'INVOICABLE' AND orderState <> 'CANCELLED'";
			if (dateLow != null) {
				command += " AND createdOn > ?";
				inserts[0] = true;
			}
			if (dateHigh != null) {
				command += " AND createdOn < ?";
				inserts[1] = true;
			}
			if (dosId != 0) {
				command += " AND dossierId = ?";
				inserts[2] = true;
			}
			if (OrdState != null) {
				command += " AND orderState = ?";
				inserts[3] = true;
			}
			if (nettoLow != 0) {
				command += " AND nettoWeight >= ?";
				inserts[4] = true;
			}
			if (nettoHigh != 0) {
				command += " AND nettoWeight <= ?";
				inserts[5] = true;
			}
			if (bruttoLow != 0) {
				command += " AND brutoWeight >= ?";
				inserts[6] = true;
			}
			if (bruttoHigh != 0) {
				command += " AND brutoWeight <= ?";
				inserts[7] = true;
			}
			if (teu != 0) {
				command += " AND teu = ?";
				inserts[8] = true;
			}
			if (company != null) {
				command += " AND shippingCompany = ?";
				inserts[9] = true;
			}
			if (compId != 0) {
				command += " AND shippingCompanyId = ?";
				inserts[10] = true;
			}
			if (compScac != null) {
				command += " AND shippingCompanyScac = ?";
				inserts[11] = true;
			}
			PreparedStatement stm = a.connection.prepareStatement("SELECT COUNT(*) FROM bookings " + command);
			int col = 1;
			if (inserts[0]) {
				stm.setObject(col,  new Timestamp(dateLow * 1000));
				col += 1;
			} if (inserts[1]) {
				stm.setObject(col,  new Timestamp(dateHigh * 1000));
				col += 1;
			} if (inserts[2]) {
				stm.setObject(col, (int) dosId);
				col += 1;
			} if (inserts[3]) {
				stm.setObject(col, (String) OrdState);
				col += 1;
			} if (inserts[4]) {
				stm.setObject(col, (int) nettoLow);
				col += 1;
			} if (inserts[5]) {
				stm.setObject(col, (int) nettoHigh);
				col += 1;
			} if (inserts[6]) {
				stm.setObject(col, (int) bruttoLow);
				col += 1;
			} if (inserts[7]) {
				stm.setObject(col, (int) bruttoHigh);
				col += 1;
			} if (inserts[8]) {
				stm.setObject(col, (int) teu);
				col += 1;
			} if (inserts[9]) {
				stm.setObject(col, (String) company);
				col += 1;
			} if (inserts[10]) {
				stm.setObject(col, (int) compId);
				col += 1;
			} if (inserts[11]) {
				stm.setObject(col, (String) compScac);
				col += 1;
			}
			ResultSet x = stm.executeQuery();
			while (x.next()) {
				i = x.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("SQL error");
			e.printStackTrace();
		}
		return i + "";
	}
	
	@GET
	@Path("/brutoWeight")
	public String brutoWeight(@QueryParam("fromD") Long dateLow,
									@QueryParam("toD") Long dateHigh, 
									@QueryParam("dosId") int dosId,
									@QueryParam("ordState") String OrdState,
									@QueryParam("teu") int teu,
									@QueryParam("shipComp") String company,
									@QueryParam("shipCompId") int compId,
									@QueryParam("shipCompScac") String compScac) {
		Statistics a = new Statistics();
		a.connectToDatabase();
		int i = -1;
		boolean[] inserts = new boolean[]{false, false, false, false, false, false, false, false};
		try {
			String command = "WHERE 0=0 AND orderState <> 'INPLANNING' AND orderState <> 'INVOICE'" + 
					"AND orderState <> 'DRAFT' AND orderState <> 'INVOICED' AND orderState <> 'INVOICABLE' AND orderState <> 'CANCELLED'";
			if (dateLow != null) {
				command += " AND createdOn > ?";
				inserts[0] = true;
			}
			if (dateHigh != null) {
				command += " AND createdOn < ?";
				inserts[1] = true;
			}
			if (dosId != 0) {
				command += " AND dossierId = ?";
				inserts[2] = true;
			}
			if (OrdState != null) {
				command += " AND orderState = ?";
				inserts[3] = true;
			}
			if (teu != 0) {
				command += " AND teu = ?";
				inserts[4] = true;
			}
			if (company != null) {
				command += " AND shippingCompany = ?";
				inserts[5] = true;
			}
			if (compId != 0) {
				command += " AND shippingCompanyId = ?";
				inserts[6] = true;
			}
			if (compScac != null) {
				command += " AND shippingCompanyScac = ?";
				inserts[7] = true;
			}
			PreparedStatement stm = a.connection.prepareStatement("SELECT SUM(brutoWeight) FROM bookings " + command);
			int col = 1;
			if (inserts[0]) {
				stm.setObject(col,  new Timestamp(dateLow * 1000));
				col += 1;
			} if (inserts[1]) {
				stm.setObject(col,  new Timestamp(dateHigh * 1000));
				col += 1;
			} if (inserts[2]) {
				stm.setObject(col, (int) dosId);
				col += 1;
			} if (inserts[3]) {
				stm.setObject(col, (String) OrdState);
				col += 1;
			} if (inserts[4]) {
				stm.setObject(col, (int) teu);
				col += 1;
			} if (inserts[5]) {
				stm.setObject(col, (String) company);
				col += 1;
			} if (inserts[6]) {
				stm.setObject(col, (int) compId);
				col += 1;
			} if (inserts[7]) {
				stm.setObject(col, (String) compScac);
				col += 1;
			}
			ResultSet x = stm.executeQuery();
			while (x.next()) {
				i = x.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("SQL error");
			e.printStackTrace();
		}
		return i + "";
	}
	
	@GET
	@Path("/nettoWeight")
	public String nettoWeight(@QueryParam("fromD") Long dateLow,
									@QueryParam("toD") Long dateHigh, 
									@QueryParam("dosId") int dosId,
									@QueryParam("ordState") String OrdState,
									@QueryParam("teu") int teu,
									@QueryParam("shipComp") String company,
									@QueryParam("shipCompId") int compId,
									@QueryParam("shipCompScac") String compScac) {
		Statistics a = new Statistics();
		a.connectToDatabase();
		int i = -1;
		boolean[] inserts = new boolean[]{false, false, false, false, false, false, false, false};
		try {
			String command = "WHERE 0=0 AND orderState <> 'INPLANNING' AND orderState <> 'INVOICE'" + 
					"AND orderState <> 'DRAFT' AND orderState <> 'INVOICED' AND orderState <> 'INVOICABLE' AND orderState <> 'CANCELLED'";
			if (dateLow != null) {
				command += " AND createdOn > ?";
				inserts[0] = true;
			}
			if (dateHigh != null) {
				command += " AND createdOn < ?";
				inserts[1] = true;
			}
			if (dosId != 0) {
				command += " AND dossierId = ?";
				inserts[2] = true;
			}
			if (OrdState != null) {
				command += " AND orderState = ?";
				inserts[3] = true;
			}
			if (teu != 0) {
				command += " AND teu = ?";
				inserts[4] = true;
			}
			if (company != null) {
				command += " AND shippingCompany = ?";
				inserts[5] = true;
			}
			if (compId != 0) {
				command += " AND shippingCompanyId = ?";
				inserts[6] = true;
			}
			if (compScac != null) {
				command += " AND shippingCompanyScac = ?";
				inserts[7] = true;
			}
			PreparedStatement stm = a.connection.prepareStatement("SELECT SUM(nettoWeight) FROM bookings " + command);
			int col = 1;
			if (inserts[0]) {
				stm.setObject(col,  new Timestamp(dateLow * 1000));
				col += 1;
			} if (inserts[1]) {
				stm.setObject(col,  new Timestamp(dateHigh * 1000));
				col += 1;
			} if (inserts[2]) {
				stm.setObject(col, (int) dosId);
				col += 1;
			} if (inserts[3]) {
				stm.setObject(col, (String) OrdState);
				col += 1;
			} if (inserts[4]) {
				stm.setObject(col, (int) teu);
				col += 1;
			} if (inserts[5]) {
				stm.setObject(col, (String) company);
				col += 1;
			} if (inserts[6]) {
				stm.setObject(col, (int) compId);
				col += 1;
			} if (inserts[7]) {
				stm.setObject(col, (String) compScac);
				col += 1;
			}
			ResultSet x = stm.executeQuery();
			while (x.next()) {
				i = x.getInt(1);
			}
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
	
}
