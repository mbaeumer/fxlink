package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LinkCreationDBHandler {
	public static final String BASE_INSERT = "INSERT INTO Link VALUES(DEFAULT, ?, ?, ?, ";
	public static final String DEFAULT_CATEGORY = "DEFAULT, ";
	public static final String CATEGORY_SET = "?, ";
	public static final String QUERY_PART_DATE = "DEFAULT, ?)";

	public static String constructSqlString(Link link){
		String sql = BASE_INSERT;

		if (link == null){
			return null;
		}

		sql = sql.replaceFirst("\\?", "'" + link.getTitle() + "'");
		sql = sql.replaceFirst("\\?", "'" + link.getURL() + "'");
		sql = sql.replaceFirst("\\?", "'" + link.getDescription() + "'");

		if (LinkCreationDBHandler.isCategorySet(link)){
			sql += CATEGORY_SET;
			sql = sql.replaceFirst("\\?", new Integer(link.getCategory().getId()).toString() );
		}else{
			sql += DEFAULT_CATEGORY;
		}

		sql += QUERY_PART_DATE;

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));
		sql = sql.replaceFirst("\\?", "'" + tsLastUpdated + "'");

		return sql;
	}

	public static boolean isCategorySet(Link link){
		return (link.getCategory() != null && link.getCategory().getName() != ValueConstants.VALUE_ALL
				&& link.getCategory().getName() != ValueConstants.VALUE_N_A);

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
