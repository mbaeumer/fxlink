package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.FollowUpStatus;
import se.mbaeumer.fxlink.models.Link;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinkReadDBHandler {

	private static String BASE_QUERY = "select l.id as linkId, l.title, l.url, l.description as linkDescription, l. created as linkCreated," +
			" l.lastUpdated  as linkLastUpdated, l.categoryId as linkCategory, l.followuprank, l.followupstatus, fus.name as followUpName, c.id as categoryId, c.name as category," +
			" c.description as categoryDescription, c.created as categoryCreated, c.lastUpdated as categoryLastUpdated" +
			" from link l left join category c on c.id = l.categoryId" +
			" left join followupstatus fus on fus.id = l.followupstatus";

	public List<Link> getAllLinksWithCategories(GenericDBHandler dbh, FollowUpStatus defaultFollowUpStatus){
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(BASE_QUERY);
			links = Collections.unmodifiableList(getDataFromResultSet(rs, defaultFollowUpStatus));
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return links;
	}

	private List<Link> getDataFromResultSet(final ResultSet rs, final FollowUpStatus defaultFollowUpStatus) throws SQLException {
		List<Link> links = new ArrayList<>();
		while (rs.next()) {
			Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
			link.setId(rs.getInt("linkId"));
			link.setCreated(rs.getTimestamp("linkCreated"));
			link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
			link.setFollowUpRank(rs.getInt("followuprank"));

			int followUpStatusId = rs.getInt("followupstatus");
			String followUpName = rs.getString("followUpName");
			if (followUpStatusId >= 1){
				FollowUpStatus followUpStatus = new FollowUpStatus();
				followUpStatus.setId(followUpStatusId);
				followUpStatus.setName(followUpName);
				link.setFollowUpStatus(followUpStatus);
			}else{
				link.setFollowUpStatus(defaultFollowUpStatus);
			}
			int categoryId = rs.getInt("categoryId");
			if (categoryId > 0){
				Category category = new Category();
				category.setId(categoryId);
				category.setName(rs.getString("category"));
				link.setCategory(category);
			}

			links.add(link);
		}

		return links;
	}
	
	public List<Link> getAllLinksByCategoryId(GenericDBHandler dbh, int categoryId, final FollowUpStatus defaultFollowUpStatus){
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();
		
		String sql = BASE_QUERY;
		if (categoryId >= 0){
			sql += " where categoryId=?";
		}
		
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			if (categoryId >= 0){
				stmt.setInt(1, categoryId);
			}			
			ResultSet rs = stmt.executeQuery();
			links = getDataFromResultSet(rs, defaultFollowUpStatus);
			/*
			while (rs.next()) {
				Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
				link.setId(rs.getInt("linkId"));
				link.setCreated(rs.getTimestamp("linkCreated"));
				link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
				link.setFollowUpRank(rs.getInt("followuprank"));
				Category category = new Category();
				category.setId(rs.getInt("categoryId"));
				category.setName(rs.getString("category"));
				link.setCategory(category);
				links.add(link);
			}
			 */
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return links;
	}

	public List<Link> getAllLinksWithNoCategory(GenericDBHandler dbh, final FollowUpStatus defaultFollowUpStatus){
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();
		
		String sql = BASE_QUERY;
		sql += " where categoryId is null";
		
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			links = getDataFromResultSet(rs, defaultFollowUpStatus);
			/*
			while (rs.next()) {
				Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
				link.setId(rs.getInt("linkId"));
				link.setCreated(rs.getTimestamp("linkCreated"));
				link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
				link.setFollowUpRank(rs.getInt("followuprank"));
				Category category = new Category();
				category.setId(rs.getInt("categoryId"));
				category.setName(rs.getString("category"));
				link.setCategory(category);
				links.add(link);
			}
			 */
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return links;
	}

	public List<Link> getAllLinksWithCategory(GenericDBHandler dbh){
		Connection connection = dbh.getConnection();
		List<Link> links = new ArrayList<Link>();

		String sql = BASE_QUERY;
		sql += " where categoryId is not null";

		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
				link.setId(rs.getInt("linkId"));
				link.setCreated(rs.getTimestamp("linkCreated"));
				link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
				link.setFollowUpRank(rs.getInt("followuprank"));
				Category category = new Category();
				category.setId(rs.getInt("categoryId"));
				category.setName(rs.getString("category"));
				link.setCategory(category);
				links.add(link);
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return links;
	}

	public List<Link> getAllLinks(GenericDBHandler dbh, final FollowUpStatus defaultFollowUpStatus){
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();
		
		try {
			PreparedStatement stmt = connection.prepareStatement(BASE_QUERY);
			ResultSet rs = stmt.executeQuery();
			links = getDataFromResultSet(rs, defaultFollowUpStatus);
			/*
			while (rs.next()) {
				Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
				link.setId(rs.getInt("linkId"));
				link.setCreated(rs.getTimestamp("linkCreated"));
				link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
				link.setFollowUpRank(rs.getInt("followuprank"));
				int categoryId = rs.getInt("categoryId");
				if (categoryId > 0){
					Category category = new Category();
					category.setId(categoryId);
					category.setName(rs.getString("category"));
					link.setCategory(category);
				}else{
					link.setCategory(null);
				}
				
				links.add(link);
			}
			 */
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return links;
	}

	public List<Link> getLinksOrderedByRank(GenericDBHandler dbh) throws SQLException {
		Connection connection = dbh.getConnection();
		List<Link> links = new ArrayList<>();

		String sql = "select l.id as linkId, l.followuprank as rank, l.title as title, l.url as url " +
				"from link l where l.followuprank > 0 order by l.followuprank";

		PreparedStatement stmt = connection.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Link link = new Link(rs.getString("title"), rs.getString("url"), null);
			link.setId(rs.getInt("linkId"));
			link.setFollowUpRank(rs.getInt("followuprank"));
			links.add(link);
		}
		stmt.close();
		rs.close();

		return links;
	}
}
