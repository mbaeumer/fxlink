package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkSearchDBHandler;
import se.mbaeumer.fxlink.models.Link;

import java.sql.SQLException;
import java.util.List;

public class SearchHandler {
	public static List<Link> findLinks(String searchTerm, boolean isUrl, boolean isTitle, boolean isDescription) throws SQLException{
		String sql = LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription);
		return LinkSearchDBHandler.findAllMatchingLinks(GenericDBHandler.getInstance(), sql, searchTerm, isUrl, isTitle, isDescription);
	}
}
