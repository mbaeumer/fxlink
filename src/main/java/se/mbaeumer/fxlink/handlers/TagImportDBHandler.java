package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Tag;

import java.sql.*;
import java.text.SimpleDateFormat;

public class TagImportDBHandler {
	public static int importTag(Tag tag, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "INSERT INTO Tag " + 
		"VALUES(?, ?, ?, ?, ?) ";
		int tagId = -1;
		
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, tag.getId());
		stmt.setString(2, tag.getName());
		stmt.setString(3, tag.getDescription());
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsCreated = Timestamp.valueOf(df.format(tag.getCreated()));
		stmt.setTimestamp(4, tsCreated);
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(tag.getLastUpdated()));
		stmt.setTimestamp(5, tsLastUpdated);
		stmt.executeUpdate();
		
		ResultSet rs = stmt.getGeneratedKeys();
		
		while (rs.next()){
			tagId = rs.getInt("id");
		}

		stmt.close();		
		return tagId;
	}


}
