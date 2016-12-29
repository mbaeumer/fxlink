package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Link;

import java.sql.*;
import java.text.SimpleDateFormat;

public class LinkImportDBHandler {
	public static int importLinksIntoDatabase(GenericDBHandler dbh, Link link) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "INSERT INTO Link "
				+ "VALUES(?, ?, ?, ?,";

		String categoryId = " DEFAULT,";
		if (link.getCategory() != null){
			categoryId = " ?,";
		}
		sql = sql + categoryId + " ?, ?) ";
		
		int linkId = -1;
		
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, link.getId());
		stmt.setString(2, link.getTitle());
		stmt.setString(3, link.getURL());
		stmt.setString(4, link.getDescription());
		
		int parameterIndex = 5;
		if (link.getCategory() != null){
			stmt.setInt(parameterIndex, link.getCategory().getId());
			parameterIndex++;
		}
		
		// created and lastUpdated
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsCreated = Timestamp.valueOf(df.format(link.getCreated()));
		stmt.setTimestamp(parameterIndex, tsCreated);
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(link.getLastUpdated()));
		stmt.setTimestamp(parameterIndex + 1 , tsLastUpdated);
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		
		while (rs.next()){
			linkId = rs.getInt("id");
		}

		stmt.close();
		return linkId;
	}

}
