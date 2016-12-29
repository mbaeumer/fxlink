package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Link;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LinkCreationDBHandler {
	public static int createLink(Link link, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		// INSERT INTO Link VALUES(DEFAULT, 'title', 'url','desc',DEFAULT,DEFAULT,DEFAULT)
		
		String sql = "INSERT INTO Link "
				+ "VALUES(DEFAULT, ?, ?, ?, DEFAULT, DEFAULT, ?) ";
		int linkId = -1;
		
		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setString(1, link.getTitle());
		stmt.setString(2, link.getURL());
		stmt.setString(3, link.getDescription());

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));
		stmt.setTimestamp(4, tsLastUpdated);
		
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		
		while (rs.next()){
			linkId = rs.getInt("id");
		}

		stmt.close();

		
/*		try {
			PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			stmt.setString(1, link.getURL());
			stmt.setString(2, link.getDescription());

			//stmt.setTimestamp(4, tsCreated);
			
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			
			
			while (rs.next()){
				linkId = rs.getInt("id");
			}

			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		*/
		return linkId;

	}
}
