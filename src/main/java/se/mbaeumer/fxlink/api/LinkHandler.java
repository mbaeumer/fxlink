package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class LinkHandler {
	public static List<Link> getLinks(){
		return LinkReadDBHandler.getAllLinksWithCategories(GenericDBHandler.getInstance());
	}
	
	public static List<Link> getLinksByCategory(Category category){
		if (category.getName() == ValueConstants.VALUE_ALL){
			return LinkReadDBHandler.getAllLinks(GenericDBHandler.getInstance());
		}else if (category.getName() == ValueConstants.VALUE_N_A){
			return LinkReadDBHandler.getAllLinksWithNoCategory(GenericDBHandler.getInstance());
		}
		return LinkReadDBHandler.getAllLinksByCategoryId(GenericDBHandler.getInstance(), category.getId());	
	}
	
	//public static List<Link> getLinksByCate
	
	public static void createLink(Link link) throws SQLException{
		String sql = LinkCreationDBHandler.constructSqlString(link);
		LinkCreationDBHandler.createLink(sql, GenericDBHandler.getInstance());
	}
	
	public static void deleteLinksByCategory(Category category){
		
	}
	
	public static void nullifyCategory(Category category){
		
	}
	
	public static void updateLink(Link link) throws ParseException, SQLException{
		String sql = LinkUpdateDBHandler.constructSqlString(link);
		LinkUpdateDBHandler.updateLink(sql, GenericDBHandler.getInstance());
	}


	public static void deleteLink(Link link) throws SQLException{
		String sql = LinkDeletionDBHandler.constructSqlString(link);
		LinkDeletionDBHandler.deleteLink(sql, GenericDBHandler.getInstance());
	}
	
	public static Link createPseudoLink(){
		Link link = new Link(ValueConstants.VALUE_NEW, ValueConstants.VALUE_NEW, ValueConstants.VALUE_NEW);
		link.setId(-1);
		link.setCategory(null);
		return link;
	}
	
	public static List<Link> getLinksWithTag(int tagId) throws SQLException{
		return LinkTagReadDBHandler.getAllLinksByTagId(GenericDBHandler.getInstance(), tagId);
	}
	
	public static void deleteAllLinks() throws SQLException{
		LinkDeletionDBHandler.deleteAllLinks(GenericDBHandler.getInstance());
	}
	
	/*
	 public static void updateLinkList(Link link){
		if (link.getId() == -1){
			LinkCreationDBHandler.createLink(link, GenericDBHandler.getInstance());
		}else{
			try {
				LinkUpdateDBHandler.updateLink(link, GenericDBHandler.getInstance());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	*/
	
	public static Link addCategoryToLink(Link link, Category category){
		
		return link;
	}
}
