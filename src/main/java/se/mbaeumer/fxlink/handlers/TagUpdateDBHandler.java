package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TagUpdateDBHandler {
	public static String SQL_BASE_UPDATE = "UPDATE Tag SET name=?, description=?, lastUpdated=? WHERE id=?";

	public static void updateCategory(Tag tag, GenericDBHandler dbh) throws ParseException, SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = SQL_BASE_UPDATE;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));

		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, tag.getName());
		stmt.setString(2, tag.getDescription());
		stmt.setTimestamp(3, tsLastUpdated);
		stmt.setInt(4, tag.getId());
		stmt.executeUpdate();
		stmt.close();
	}

}
