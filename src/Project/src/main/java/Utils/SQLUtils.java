package Utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.FileWriter;
import java.io.IOException;  

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

	
	public static void exportToCSV(ResultSet rs, String filename) {
		if (rs == null) {
			System.out.println("No result!");
			return ;
		}

		try {
			  FileWriter fw = new FileWriter(filename);
			  
			   ResultSetMetaData rsmd = rs.getMetaData();
			   int columnsNumber = rsmd.getColumnCount();
			   
			   // print column name
			    for (int i = 1; i <= columnsNumber; i++) {
			           if (i > 1) { 
			        	   fw.append(", ");
			           }
			           fw.append(rsmd.getColumnName(i));
		        }
			    fw.append("\n");
			    
			   // print every row
			   while (rs.next()) {
			       for (int i = 1; i <= columnsNumber; i++) {
			           if (i > 1) {
			        	   fw.append(", ");
			           }
			           String columnValue = rs.getString(i);
			           fw.append(columnValue);
			       }
			       fw.append("\n");
			   }	
			   
			   fw.close();
			   rs.close();
			   
		       System.out.println("Success! Your file here: " +
		    		   				System.getProperty("user.dir"));
		} catch (SQLException | IOException e) {
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
