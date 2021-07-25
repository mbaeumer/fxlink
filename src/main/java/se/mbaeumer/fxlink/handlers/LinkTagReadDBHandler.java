package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.LinkTag;
import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinkTagReadDBHandler {
	public List<Link> getAllLinksByTagId(GenericDBHandler dbh, int tagId) throws SQLException{
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();
		
		String sql = "select l.id as linkId, l.title l.url, l.description as linkDescription, l.created as linkCreated, l.lastUpdated as linklLastUpdated, lt.tagid as tagid" +
		" from linktag lt" + 
		" left join link l on lt.linkId=l.id" +
		" where tagid=?";
		
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, tagId);
				
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Link link = new Link(rs.getString("title"), rs.getString("url"), rs.getString("linkDescription"));
			link.setId(rs.getInt("linkId"));
			link.setCreated(rs.getTimestamp("linkCreated"));
			link.setLastUpdated(rs.getTimestamp("lastUpdated"));
			links.add(link);
		}
		stmt.close();
		rs.close();
				
		return links;
	}
	
	public static List<Tag> getAllTagsForLink(GenericDBHandler dbh, Link link) throws SQLException{
		Connection connection = dbh.getConnection();				
		List<Tag> tags = new ArrayList<Tag>();
		
		String sql = "select lt.id as ltId, l.id as linkId, l.url, l.description, lt.linkid, lt.tagid as tagId, t.name as tagname" + 
					" from linktag lt" +
					" left join link l on lt.linkid = l.id" +
					" left join tag t on lt.tagid = t.id" +
					" where linkId=?";
				
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, link.getId());
				
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Tag tag = new Tag();
			tag.setId(rs.getInt("tagId"));
			tag.setName(rs.getString("tagname"));
			tags.add(tag);
		}
		stmt.close();
		rs.close();
		
		return tags;
	}
	
	public static List<LinkTag> getAllLinkTagEntries(GenericDBHandler dbh) throws SQLException{
		List<LinkTag> linkTags = new ArrayList<LinkTag>();
		
		Connection connection = dbh.getConnection();				
		
		String sql = "select lt.id as id, lt.linkid as linkId, lt.tagid as tagId" + 
					" from linktag lt";
				
		PreparedStatement stmt = connection.prepareStatement(sql);
				
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			LinkTag tag = new LinkTag();
			tag.setId(rs.getInt("id"));
			tag.setLinkId(rs.getInt("linkid"));
			tag.setTagId(rs.getInt("tagid"));
			linkTags.add(tag);
		}
		stmt.close();
		rs.close();
		
		return linkTags;
	}
}
