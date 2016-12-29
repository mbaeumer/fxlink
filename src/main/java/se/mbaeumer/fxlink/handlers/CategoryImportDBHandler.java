package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class CategoryImportDBHandler {
	public static void importCategory(Category category, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "INSERT INTO Category " + 
		"VALUES(?, ?, ?, ?, ?) ";

		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, category.getId());
		stmt.setString(2, category.getName());
		stmt.setString(3, category.getDescription());
		
		//created and lastUpdated
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsCreated = Timestamp.valueOf(df.format(category.getCreated()));
		stmt.setTimestamp(4, tsCreated);
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(category.getLastUpdated()));
		stmt.setTimestamp(5, tsLastUpdated);
		
		stmt.executeUpdate();
		stmt.close();
	}
}
