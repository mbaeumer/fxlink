package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LinkTagCreationDBHandler {
	public static int addTagToLink(Tag tag, Link link, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		// INSERT INTO LinkTag VALUES(DEFAULT,linkId,tagId)
		
		String sql = "INSERT INTO LinkTag "
				+ "VALUES(DEFAULT, ?, ?) ";
		int linkId = -1;
		
		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, link.getId());
		stmt.setInt(2, tag.getId());

		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		
		
		while (rs.next()){
			linkId = rs.getInt("id");
		}

		stmt.close();
		
		return linkId;

		
	}
}
