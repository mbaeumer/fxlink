package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LinkTagDeletionDBHandler {
	
	public static void deleteTagFromLink(Tag tag, Link link, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM LinkTag WHERE linkId=? AND tagId=?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, link.getId());
		stmt.setInt(2, tag.getId());

		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void deleteAllLinkTags(GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM LinkTag";
		PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.executeUpdate();
		stmt.close();
		
	}
}
