package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fasterxml.jackson.databind.jsonschema.JsonSchema;

import utils.Converter;

// dao is for data access model, 
// so it is likely to be the gateway to database
public enum MovieDao {
	instance;
	
	private Connection connection;
	
	// a private constructor, make db connection when initialized
	private MovieDao() {
		// load driver
		try {
			Class.forName("org.postgresql.Driver");
		}	catch (ClassNotFoundException cnfe) {
			System.err.println("Error loading driver: " + cnfe);
		}
		
		// compose url
		String host = "castle.ewi.utwente.nl";
		String dbName = "di095";
		String url = "jdbc:postgresql://" + host + ":5432/" + dbName;
		String username = "di095";
		String password = "SX4lAYE1";
		
		// try connection
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch(SQLException sqle) {
			System.err.println("Error connection: " + sqle);
		} 
	}
	
	public String getMovieCountByGenre() {
		String query = "SELECT genre , COUNT(mid) FROM genre\n" + 
					   "GROUP BY genre\n" + 
					   "ORDER BY COUNT(mid) DESC LIMIT 6";
		
//		String json = "{";
		ResultSet resultSet = null;
		String jsonString = "";
		try {
			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(query);

			jsonString = Converter.convertToJSON(resultSet).toString();
			System.out.println(jsonString);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

//		return json;
		return jsonString;
		
	}

}
