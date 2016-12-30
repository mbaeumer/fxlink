package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Link;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LinkCreationDBHandler {
	public static final String BASE_INSERT = "INSERT INTO Link VALUES(DEFAULT, ?, ?, ?, DEFAULT, DEFAULT, ?)";

	public static String constructSqlString(Link link){
		String sql = BASE_INSERT;

		if (link == null){
			return null;
		}

		sql = sql.replaceFirst("\\?", "'" + link.getTitle() + "'");
		sql = sql.replaceFirst("\\?", "'" + link.getURL() + "'");
		sql = sql.replaceFirst("\\?", "'" + link.getDescription() + "'");

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));
		sql = sql.replaceFirst("\\?", "'" + tsLastUpdated + "'");

		return sql;
	}

	public static int createLink(String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		

		int linkId = -1;
		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();

		while (rs.next()){
			linkId = rs.getInt("id");
		}
		stmt.close();

		return linkId;
	}
}
