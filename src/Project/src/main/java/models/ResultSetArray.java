package models;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetArray {
	/**
	 * Changes ResultSet data into string array that is saved as follows: label;label;label|value;value;value
	 * this is way shorter than xml or json representation and therefore faster
	 */
	
	/**
	 * Function to parse the value of different column with maximum parse until 3 differents columns (sql database)
	 */
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
	
	/**
	 * change the value of the sql into string value as an array
	 */
	public static String resultSetToStringArray(ResultSet rs) {
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
}
