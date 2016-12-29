package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TagCreationDBHandler {
	public static int createTag(Tag tag, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "INSERT INTO Tag " + 
		"VALUES(DEFAULT, ?, ?, DEFAULT, DEFAULT) ";
		int tagId = -1;
		
		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setString(1, tag.getName());
		stmt.setString(2, tag.getDescription());
		
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		
		while (rs.next()){
			tagId = rs.getInt("id");
		}

		stmt.close();		
		return tagId;
	}

}
