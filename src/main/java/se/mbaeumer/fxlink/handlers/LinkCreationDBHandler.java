package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class LinkCreationDBHandler {
	public static final String BASE_INSERT = "INSERT INTO Link VALUES(DEFAULT, TITLE_PLACEHOLDER, URL_PLACEHOLDER, " +
			"DESCRIPTION_PLACEHOLDER, ";
	public static final String DEFAULT_CATEGORY = "DEFAULT, ";
	public static final String CATEGORY_SET = "CATEGORY_PLACEHOLDER, ";
	public static final String QUERY_PART_DATE = "DEFAULT, DATE_PLACEHOLDER, RANK_PLACEHOLDER, FOLLOWUPSTATUS_PLACEHOLDER, NULL)";

	public String constructSqlString(Link link){
		String sql = BASE_INSERT;

		if (link == null){
			return null;
		}

		String rankValue = Integer.toString(link.getFollowUpRank());
		if (link.getFollowUpRank() <= 0){
			rankValue = "DEFAULT";
		}

		sql = sql.replaceFirst(Pattern.quote("TITLE_PLACEHOLDER"), "'" + link.getTitle() + "'");
		sql = sql.replaceFirst(Pattern.quote("URL_PLACEHOLDER"), "'" + link.getURL() + "'");
		sql = sql.replaceFirst(Pattern.quote("DESCRIPTION_PLACEHOLDER"), "'" + link.getDescription() + "'");


		if (isCategorySet(link)){
			sql += CATEGORY_SET;
			sql = sql.replaceFirst(Pattern.quote("CATEGORY_PLACEHOLDER"), Integer.valueOf(link.getCategory().getId()).toString() );
		}else{
			sql += DEFAULT_CATEGORY;
		}

		sql += QUERY_PART_DATE;

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));
		sql = sql.replaceFirst(Pattern.quote("DATE_PLACEHOLDER"), "'" + tsLastUpdated + "'");
		sql = sql.replaceFirst(Pattern.quote("RANK_PLACEHOLDER"), rankValue);
		sql = sql.replaceFirst(Pattern.quote("FOLLOWUPSTATUS_PLACEHOLDER"), Integer.valueOf(link.getFollowUpStatus().getId()).toString());
		return sql;
	}

	public boolean isCategorySet(Link link){
		return (link.getCategory() != null && !ValueConstants.VALUE_ALL.equals(link.getCategory().getName())
				&& !ValueConstants.VALUE_N_A.equals(link.getCategory().getName()));
	}

	public int createLink(String sql, GenericDBHandler dbh) throws SQLException{
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