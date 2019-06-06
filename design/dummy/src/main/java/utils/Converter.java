package utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility for converting ResultSets into some Output formats
 */
 public class Converter {
    /**
     * Convert a result set into a JSON Array
     * @param resultSet
     * @return a JSONArray
     * @throws Exception
     */
    public static JSONArray convertToJSON(ResultSet resultSet) {
        JSONArray jsonArray = new JSONArray();
        
        try {
        	int colsNum = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
            	// one JSON obj for each row in ResultSet
            	JSONObject obj = new JSONObject();
                for (int i = 0; i < colsNum; i++) {
                    obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                            .toLowerCase(), resultSet.getObject(i + 1));
                }
                jsonArray.put(obj);
            }
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        
        return jsonArray;
    }
 }
