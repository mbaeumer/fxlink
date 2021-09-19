package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

import java.sql.*;

public class CategoryCreationDBHandler {
	public static final String BASE_INSERT = "INSERT INTO Category VALUES(DEFAULT, ?, ?, DEFAULT, DEFAULT)";

	public String constructSqlString(Category category){
		String sql = BASE_INSERT;
		if (category == null){
			return null;
		}

		sql = sql.replaceFirst("\\?", "'" + category.getName() + "'");
		sql = sql.replaceFirst("\\?", "'" + category.getDescription() + "'");
		return sql;
	}

	public int createCategory(String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();

		int categoryId = -1;
		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		
		while (rs.next()){
			categoryId = rs.getInt("id");
		}
		stmt.close();
		
		return categoryId;
	}
}
