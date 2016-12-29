package se.mbaeumer.fxlink.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManagedItemDBHandler {
	public static List<String> getAllManagedItems(GenericDBHandler dbh){
		Connection connection = dbh.getConnection();				
		List<String> items = new ArrayList<String>();
		
		String sql = "select name from ManagedItem";
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				items.add(rs.getString("name"));				
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}
}
