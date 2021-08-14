package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class LinkHandler {

	private final LinkReadDBHandler linkReadDBHandler;
	private final LinkTagReadDBHandler linkTagReadDBHandler;
	private final LinkCreationDBHandler linkCreationDBHandler;
	private final LinkUpdateDBHandler linkUpdateDBHandler;
	private final LinkDeletionDBHandler linkDeletionDBHandler;

	public LinkHandler(LinkReadDBHandler linkReadDBHandler, LinkTagReadDBHandler linkTagReadDBHandler, LinkCreationDBHandler linkCreationDBHandler, LinkUpdateDBHandler linkUpdateDBHandler, LinkDeletionDBHandler linkDeletionDBHandler) {
		this.linkReadDBHandler = linkReadDBHandler;
		this.linkTagReadDBHandler = linkTagReadDBHandler;
		this.linkCreationDBHandler = linkCreationDBHandler;
		this.linkUpdateDBHandler = linkUpdateDBHandler;
		this.linkDeletionDBHandler = linkDeletionDBHandler;
	}

	public List<Link> getLinks(){
		return this.linkReadDBHandler.getAllLinksWithCategories(GenericDBHandler.getInstance());
	}
	
	public List<Link> getLinksByCategory(Category category){
		if (ValueConstants.VALUE_ALL.equals(category.getName())){
			return this.linkReadDBHandler.getAllLinks(GenericDBHandler.getInstance());
		}else if (ValueConstants.VALUE_N_A.equals(category.getName())){
			return this.linkReadDBHandler.getAllLinksWithNoCategory(GenericDBHandler.getInstance());
		}
		return this.linkReadDBHandler.getAllLinksByCategoryId(GenericDBHandler.getInstance(), category.getId());
	}
	
	public void createLink(Link link) throws SQLException{
		String sql = linkCreationDBHandler.constructSqlString(link);
		linkCreationDBHandler.createLink(sql, GenericDBHandler.getInstance());
	}
	
	public void updateLink(Link link) throws ParseException, SQLException{
		String sql = linkUpdateDBHandler.constructSqlString(link);
		linkUpdateDBHandler.updateLink(sql, GenericDBHandler.getInstance());
	}

	public void deleteLink(Link link) throws SQLException{
		String sql = linkDeletionDBHandler.constructSqlString(link);
		linkDeletionDBHandler.deleteLink(sql, GenericDBHandler.getInstance());
	}
	
	public static Link createPseudoLink(){
		Link link = new Link(ValueConstants.VALUE_NEW, ValueConstants.VALUE_NEW, ValueConstants.VALUE_NEW);
		link.setId(-1);
		link.setCategory(null);
		return link;
	}
	
	public List<Link> getLinksWithTag(int tagId) throws SQLException{
		return this.linkTagReadDBHandler.getAllLinksByTagId(GenericDBHandler.getInstance(), tagId);
	}
	
	public static void deleteAllLinks() throws SQLException{
		LinkDeletionDBHandler.deleteAllLinks(GenericDBHandler.getInstance());
	}
}