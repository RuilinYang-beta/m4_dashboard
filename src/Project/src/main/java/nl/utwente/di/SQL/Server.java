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
		ResultSet x = a.execute("SELECT date, counter FROM st_book LIMIT 5;");
		return parseToJSON(x);
	}
	
	public static String parseToJSON(ResultSet set) {
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
	@Path("/bookings")
	public String bookingsPerDate(@QueryParam("from") Long dateLow,
									@QueryParam("to") Long dateHigh, 
									@QueryParam("dosId") int dosId) {
		Statistics a = new Statistics();
		a.connectToDatabase();
		int i = -1;
		boolean[] inserts = new boolean[]{false, false, false};
		try {
			String command = "WHERE 0=0";
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
