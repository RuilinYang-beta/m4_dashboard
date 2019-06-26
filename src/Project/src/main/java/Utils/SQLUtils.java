package Utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SQLUtils {
	
	/**
	 * Display the given ResultSet obj in console, 
	 * for a quick visual inspect.
	 */
	public static void displayResultSet(ResultSet rs) {
		if (rs == null) {
			System.out.println("No result!");
			return ;
		}
		
		try {
			   ResultSetMetaData rsmd = rs.getMetaData();
			   int columnsNumber = rsmd.getColumnCount();
			   
			   // print column name
			    for (int i = 1; i <= columnsNumber; i++) {
			           if (i > 1) { 
			        	   System.out.print(",  ");
			           }
			           System.out.print(rsmd.getColumnName(i));
		        }
			    System.out.println("");
			    
			   // print every row
			   while (rs.next()) {
			       for (int i = 1; i <= columnsNumber; i++) {
			           if (i > 1) System.out.print(",  ");
			           String columnValue = rs.getString(i);
			           System.out.print(columnValue);
			       }
			       System.out.println("");
			   }		
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Glue one column into one string.
	 * @requires rs has only one column
	 */
	public static String glueColumnIntoString(ResultSet rs) {
		if (rs == null) {
			System.out.println("No result!");
			return "No result";
		}
		
		String result = "";
		try {
			   // print every row
			   while (rs.next()) {
			           result += rs.getString(1);
			           result += ";";
			       }

			   result = result.substring(0, result.length() - 1);		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
}
