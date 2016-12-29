package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryCreationDBHandler {
	public static int createCategory(Category category, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "INSERT INTO Category " + 
		"VALUES(DEFAULT, ?, ?, DEFAULT, DEFAULT) ";
		int categoryId = -1;
		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setString(1, category.getName());
		stmt.setString(2, category.getDescription());
		
		
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		
		while (rs.next()){
			categoryId = rs.getInt("id");
		}

		stmt.close();
		
		return categoryId;
	}
}
