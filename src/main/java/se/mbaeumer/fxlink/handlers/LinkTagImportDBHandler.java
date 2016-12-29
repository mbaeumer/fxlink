package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.LinkTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LinkTagImportDBHandler {
	public static void importLinkTag(LinkTag linkTag, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		// INSERT INTO LinkTag VALUES(DEFAULT,linkId,tagId)
		
		String sql = "INSERT INTO LinkTag "
				+ "VALUES(?, ?, ?) ";
		
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, linkTag.getId());
		stmt.setInt(2, linkTag.getLinkId());
		stmt.setInt(3, linkTag.getTagId());

		stmt.executeUpdate();

		stmt.close();
	}
}
