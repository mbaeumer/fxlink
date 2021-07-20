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

	public static String constructSqlString(Tag tag){
		if (tag == null){
			return null;
		}
		String sql = SQL_BASE_UPDATE;

		sql = sql.replaceFirst("\\?", "'" + tag.getName() + "'");
		sql = sql.replaceFirst("\\?", "'" + tag.getDescription() + "'");

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));
		sql = sql.replaceFirst("\\?", "'" + tsLastUpdated + "'");

		sql = sql.replaceFirst("\\?", Integer.valueOf(tag.getId()).toString() );

		return sql;
	}

	public static void updateCategory(String sql, GenericDBHandler dbh) throws ParseException, SQLException{
		Connection connection = dbh.getConnection();

		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

		stmt.executeUpdate();
		stmt.close();
	}
}