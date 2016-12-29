package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TagDeletionDBHandler {
	public static void deleteLink(Tag tag, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM Tag WHERE id=?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, tag.getId());

		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void deleteAllTags(GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM Tag";
		PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.executeUpdate();
		stmt.close();
		
	}

}
