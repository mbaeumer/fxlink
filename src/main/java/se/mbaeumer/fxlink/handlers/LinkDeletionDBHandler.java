package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LinkDeletionDBHandler {
	public static void deleteLink(Link link, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM Link WHERE id=?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, link.getId());

		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void deleteLinksWithCategory(Category category, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM Link WHERE categoryId=?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, category.getId());

		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void deleteAllLinks(GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM Link";
		PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.executeUpdate();
		stmt.close();

	}

}
