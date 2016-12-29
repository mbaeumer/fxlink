package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CategoryUpdateDBHandler {
	public static String SQL_BASE_UPDATE = "UPDATE Category SET name=?, description=?, lastUpdated=? WHERE id=?";

	public static void updateCategory(Category link, GenericDBHandler dbh) throws ParseException, SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = SQL_BASE_UPDATE;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));

		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, link.getName());
		stmt.setString(2, link.getDescription());
		stmt.setTimestamp(3, tsLastUpdated);
		stmt.setInt(4, link.getId());
		stmt.executeUpdate();
		stmt.close();
	}
}
