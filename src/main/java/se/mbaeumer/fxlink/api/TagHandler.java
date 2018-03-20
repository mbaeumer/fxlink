package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Tag;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class TagHandler {
	public static List<Tag> getTags(){
		TagDBHandler tagDBHandler = new TagDBHandler();
		String sql = tagDBHandler.constructSqlString(null);
		return tagDBHandler.getAllTags(sql, new HsqldbConnectionHandler());
	}
	
	public static void createTag(Tag tag) throws SQLException{
		String sql = TagCreationDBHandler.constructSqlString(tag);
		TagCreationDBHandler.createTag(sql, GenericDBHandler.getInstance());
	}
	
	public static void updateTag(Tag tag) throws ParseException, SQLException{
		String sql = TagUpdateDBHandler.constructSqlString(tag);
		TagUpdateDBHandler.updateCategory(sql, GenericDBHandler.getInstance());
	}
	
	public static void deleteTag(Tag tag) throws SQLException{
		String sql = TagDeletionDBHandler.constructSqlString(tag);
		TagDeletionDBHandler.deleteTag(sql, GenericDBHandler.getInstance());
	}
	
	public static Tag createPseudoTag(){
		Tag tag = new Tag();
		tag.setId(-1);
		tag.setDescription(ValueConstants.VALUE_NEW);
		tag.setName(ValueConstants.VALUE_NEW);
		tag.setCreated(new Date());
		tag.setLastUpdated(new Date());
		return tag;
	}
	
	public static void addTagToLink(Tag tag, Link link) throws SQLException{
		LinkTagCreationDBHandler.addTagToLink(tag, link, GenericDBHandler.getInstance());
	}
	
	public static void removeTagToLink(Tag tag, Link link) throws SQLException{
		LinkTagDeletionDBHandler.deleteTagFromLink(tag, link, GenericDBHandler.getInstance());
	}
	
	public static List<Tag> getAllTagsForLink(Link link) throws SQLException{
		return LinkTagReadDBHandler.getAllTagsForLink(GenericDBHandler.getInstance(), link);
	}
	
	public static void deleteAllTags() throws SQLException{
		TagDeletionDBHandler.deleteAllTags(GenericDBHandler.getInstance());
	}
}
