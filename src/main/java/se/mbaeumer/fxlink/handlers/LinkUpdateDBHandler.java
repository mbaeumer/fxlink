package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class LinkUpdateDBHandler {
	public static String SQL_BASE_UPDATE = "UPDATE Link SET title=TITLE_PLACEHOLDER, url=URL_PLACEHOLDER, description=DESCRIPTION_PLACEHOLDER, " +
			"followuprank=RANK_PLACEHOLDER, followupstatus=FOLLOWUPSTATUS_PLACEHOLDER,";
	public static String SQL_UPDATE_CATEGORY = "categoryId=CATEGORY_ID_PLACEHOLDER,";
	public static String SQL_UPDATE_DATE = "lastUpdated=DATE_PLACEHOLDER ";
	public static String SQL_UPDATE_WHERE_CLAUSE = "WHERE id=ID_PLACEHOLDER";
	public static final String DEFAULT_CATEGORY = "categoryId=null,";

	public static final String DEFAULT_FOLLOWUP_DATE="followupdate=null, ";

	public static final String SQL_UPDATE_FOLLOWUP_DATE="followupdate=FOLLOWUP_DATE_PLACEHOLDER, ";

	public static String SQL_BASE_MOVE = "UPDATE Link SET categoryId=CATEGORY_ID_PLACEHOLDER WHERE categoryId=CATEGORY_ID_PLACEHOLDER";

	public String constructSqlString(Link link){
		String  sql = SQL_BASE_UPDATE;

		if (link == null){
			return null;
		}

		if (link.getCategory() != null && link.getCategory().getId() > 0){
			sql += SQL_UPDATE_CATEGORY;
		}else{
			sql += DEFAULT_CATEGORY;
		}

		if (link.getFollowUpDate() != null){
			sql += SQL_UPDATE_FOLLOWUP_DATE;
		}else {
			sql += DEFAULT_FOLLOWUP_DATE;
		}

		sql += SQL_UPDATE_DATE + SQL_UPDATE_WHERE_CLAUSE;


		sql = sql.replaceFirst(Pattern.quote("TITLE_PLACEHOLDER"), "'" + link.getTitle() + "'");
		sql = sql.replaceFirst(Pattern.quote("URL_PLACEHOLDER"), "'" + link.getURL() + "'");
		sql = sql.replaceFirst(Pattern.quote("DESCRIPTION_PLACEHOLDER"), "'" + link.getDescription() + "'");
		sql = sql.replaceFirst(Pattern.quote("RANK_PLACEHOLDER"), Integer.valueOf(link.getFollowUpRank()).toString());
		sql = sql.replaceFirst(Pattern.quote("FOLLOWUPSTATUS_PLACEHOLDER"), Integer.valueOf(link.getFollowUpStatus().getId()).toString());

		if (link.getCategory() != null){
			sql = sql.replaceFirst(Pattern.quote("CATEGORY_ID_PLACEHOLDER"), Integer.valueOf(link.getCategory().getId()).toString() );
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (link.getFollowUpDate() != null) {
			Timestamp tsFollowUpDate = Timestamp.valueOf(df.format(link.getFollowUpDate()));
			sql = sql.replaceFirst(Pattern.quote("FOLLOWUP_DATE_PLACEHOLDER"), "'" + tsFollowUpDate + "'");
		}

		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));
		sql = sql.replaceFirst(Pattern.quote("DATE_PLACEHOLDER"), "'" + tsLastUpdated + "'");

		sql = sql.replaceFirst(Pattern.quote("ID_PLACEHOLDER"), Integer.valueOf(link.getId()).toString() );

		return sql;
	}

	public void updateLink(String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();

		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		
		stmt.executeUpdate();
		stmt.close();
	}
	
	public String constructSqlStringMoveLink(Category source, Category target){

		if (source == null || target == null ){
			return null;
		}

		String sql = SQL_BASE_MOVE;

		sql = sql.replaceFirst(Pattern.quote("CATEGORY_ID_PLACEHOLDER"), Integer.valueOf(target.getId()).toString() );
		sql = sql.replaceFirst(Pattern.quote("CATEGORY_ID_PLACEHOLDER"), Integer.toString(source.getId()));

		return sql;
	}

	public void moveLinks(String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();

		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.executeUpdate();
		stmt.close();
	}

	public void updateRank(Link link, int rank, GenericDBHandler dbh) throws SQLException {

		Connection connection = dbh.getConnection();
		String sql = "UPDATE Link SET followuprank=RANK_PLACEHOLDER, followupstatus=FOLLOWUPSTATUS_PLACEHOLDER " + SQL_UPDATE_WHERE_CLAUSE;

		sql = sql.replaceFirst(Pattern.quote("RANK_PLACEHOLDER"), Integer.valueOf(rank).toString());
		sql = sql.replaceFirst(Pattern.quote("FOLLOWUPSTATUS_PLACEHOLDER"), Integer.valueOf(link.getFollowUpStatus().getId()).toString());
		sql = sql.replaceFirst(Pattern.quote("ID_PLACEHOLDER"), Integer.valueOf(link.getId()).toString());

		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.executeUpdate();
		stmt.close();

	}
}
