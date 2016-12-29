package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CategoryDeletionDBHandler {
	public static void deleteCategory(Category category, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM Category WHERE id=?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, category.getId());

		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void deleteAllCategories(GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM Category";
		PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.executeUpdate();
		stmt.close();
		
	}

}
