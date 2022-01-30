package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkSearchDBHandler;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;

import java.sql.SQLException;
import java.util.List;

public class SearchHandler {
	private final LinkSearchDBHandler linkSearchDBHandler;

	public SearchHandler(LinkSearchDBHandler linkSearchDBHandler) {
		this.linkSearchDBHandler = linkSearchDBHandler;
	}

	public List<Link> findLinks(String searchTerm, boolean isUrl, boolean isTitle, boolean isDescription, Category category) throws SQLException{
		String sql = linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, category);
		return linkSearchDBHandler.findAllMatchingLinks(GenericDBHandler.getInstance(), sql, category);
	}
}
