package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LinkReadDBHandler {
	public List<Link> getAllLinksWithCategories(GenericDBHandler dbh){
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();
		
		String sql = "select l.id as linkId, l.title, l.url, l.description as linkDescription, l. created as linkCreated," + 
		" l.lastUpdated  as linkLastUpdated, l.categoryId as linkCategory, c.id as categoryId, c.name as category," + 
		" c.description as categoryDescription, c.created as categoryCreated, c.lastUpdated as categoryLastUpdated" +
		" from link l left join category c on c.id = l.categoryId";
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
				link.setId(rs.getInt("linkId"));
				link.setCreated(rs.getTimestamp("linkCreated"));
				link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
				
				int categoryId = rs.getInt("categoryId");
				if (categoryId > 0){
					Category category = new Category();
					category.setId(categoryId);
					category.setName(rs.getString("category"));
					link.setCategory(category);
				}
				
				links.add(link);
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return links;
	}
	
	public List<Link> getAllLinksByCategoryId(GenericDBHandler dbh, int categoryId){
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();
		
		String sql = "select l.id as linkId, l.url, l.title, l.description as linkDescription, l. created as linkCreated," + 
		" l.lastUpdated  as linkLastUpdated, l.categoryId as linkCategory, c.id as categoryId, c.name as category," + 
		" c.description as categoryDescription, c.created as categoryCreated, c.lastUpdated as categoryLastUpdated" +
		" from link l left join category c on c.id = l.categoryId";
		
		if (categoryId >= 0){
			sql += " where categoryId=?";
		}
		
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			if (categoryId >= 0){
				stmt.setInt(1, categoryId);
			}			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
				link.setId(rs.getInt("linkId"));
				link.setCreated(rs.getTimestamp("linkCreated"));
				link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
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

	public List<Link> getAllLinksWithNoCategory(GenericDBHandler dbh){
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();
		
		String sql = "select l.id as linkId, l.title, l.url, l.description as linkDescription, l. created as linkCreated," + 
		" l.lastUpdated  as linkLastUpdated, l.categoryId as linkCategory, c.id as categoryId, c.name as category," + 
		" c.description as categoryDescription, c.created as categoryCreated, c.lastUpdated as categoryLastUpdated" +
		" from link l left join category c on c.id = l.categoryId";		
		sql += " where categoryId is null";
		
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
				link.setId(rs.getInt("linkId"));
				link.setCreated(rs.getTimestamp("linkCreated"));
				link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
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

	public List<Link> getAllLinksWithCategory(GenericDBHandler dbh){
		Connection connection = dbh.getConnection();
		List<Link> links = new ArrayList<Link>();

		String sql = "select l.id as linkId, l.title, l.url, l.description as linkDescription, l. created as linkCreated," +
				" l.lastUpdated  as linkLastUpdated, l.categoryId as linkCategory, c.id as categoryId, c.name as category," +
				" c.description as categoryDescription, c.created as categoryCreated, c.lastUpdated as categoryLastUpdated" +
				" from link l left join category c on c.id = l.categoryId";
		sql += " where categoryId is not null";

		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
				link.setId(rs.getInt("linkId"));
				link.setCreated(rs.getTimestamp("linkCreated"));
				link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
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

	public List<Link> getAllLinks(GenericDBHandler dbh){
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();
		
		String sql = "select l.id as linkId, l.title, l.url, l.description as linkDescription, l. created as linkCreated," + 
		" l.lastUpdated  as linkLastUpdated, l.categoryId as linkCategory, c.id as categoryId, c.name as category," + 
		" c.description as categoryDescription, c.created as categoryCreated, c.lastUpdated as categoryLastUpdated" +
		" from link l left join category c on c.id = l.categoryId";
		
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
				link.setId(rs.getInt("linkId"));
				link.setCreated(rs.getTimestamp("linkCreated"));
				link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
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
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return links;
	}

	
	
	
}
