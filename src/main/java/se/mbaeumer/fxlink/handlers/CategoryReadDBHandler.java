package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryReadDBHandler {
	private static final String SELECT_BY_NAME = "select c.id as categoryId, c.name, c.description from category c where c.name='";

	public String constructSqlString(final String categoryName){
		return SELECT_BY_NAME  + categoryName + "'";
	}

	// TODO: Get rid of printStackTrace
	public List<Category> getAllCategories(GenericDBHandler dbh){
		Connection connection = dbh.getConnection();				
		List<Category> categories = new ArrayList<>();
		
		String sql = "select c.id as categoryId, c.name, c.description, c.created, c.lastUpdated " +
					"from category c order by c.name asc";
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Category category = new Category();
				category.setId(rs.getInt("categoryId"));
				category.setName(rs.getString("name"));
				category.setDescription(rs.getString("description"));
				category.setCreated(rs.getTimestamp("created"));
				category.setLastUpdated(rs.getTimestamp("lastUpdated"));
				categories.add(category);
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categories;
	}

	public Category getCategoryByName(final String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		Category category = null;

		PreparedStatement stmt = connection.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			category = new Category();
			category.setId(rs.getInt("categoryId"));
			category.setName(rs.getString("name"));
			category.setDescription(rs.getString("description"));
		}
		stmt.close();
		rs.close();

		return category;
	}
}
