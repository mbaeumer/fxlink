package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryReadDBHandler {
	public static List<Category> getAllCategories(GenericDBHandler dbh){
		Connection connection = dbh.getConnection();				
		List<Category> categories = new ArrayList<Category>();
		
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

}
