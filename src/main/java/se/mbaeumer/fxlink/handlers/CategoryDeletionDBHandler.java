package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CategoryDeletionDBHandler {
	public static final String SQL_BASE_DELETE = "DELETE FROM Category ";
	public static final String WHERE_CLAUSE = "WHERE ID=?";

	public static String constructSqlString(Category category){
		if (category == null){
			return null;
		}

		String sql = SQL_BASE_DELETE + WHERE_CLAUSE;
		sql = sql.replaceFirst("\\?", new Integer(category.getId()).toString() );

		return sql;
	}

	public static void deleteCategory(String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();

		PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.executeUpdate();
		stmt.close();

	}
	
	public static void deleteAllCategories(GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		PreparedStatement stmt = connection.prepareStatement(SQL_BASE_DELETE);

		stmt.executeUpdate();
		stmt.close();
		
	}

}
