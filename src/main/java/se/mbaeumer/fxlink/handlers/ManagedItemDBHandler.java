package se.mbaeumer.fxlink.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManagedItemDBHandler implements ManagedItemDBOperationHandler{

	@Override
	public String constructSqlString(Object object) {
		return "select name from ManagedItem";
	}

	@Override
	public List<String> getAllManagedItems(String sql, DatabaseConnectionHandler databaseConnectionHandler) {
		Connection connection = databaseConnectionHandler.getConnection();
		List<String> items = new ArrayList<>();

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
