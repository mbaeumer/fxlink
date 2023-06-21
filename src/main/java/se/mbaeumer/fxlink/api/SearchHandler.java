package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.FollowUpStatusReadDBHandler;
import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkSearchDBHandler;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.FollowUpStatus;
import se.mbaeumer.fxlink.models.Link;

import java.sql.SQLException;
import java.util.List;

public class SearchHandler {
	private final LinkSearchDBHandler linkSearchDBHandler;

	private final FollowUpStatusReadDBHandler followUpStatusReadDBHandler;

	public SearchHandler(LinkSearchDBHandler linkSearchDBHandler, FollowUpStatusReadDBHandler followUpStatusReadDBHandler) {
		this.linkSearchDBHandler = linkSearchDBHandler;
		this.followUpStatusReadDBHandler = followUpStatusReadDBHandler;
	}

	public List<Link> findLinks(String searchTerm, boolean isUrl, boolean isTitle, boolean isDescription, Category category) throws SQLException{
		List<FollowUpStatus> followUpStatuses = followUpStatusReadDBHandler.getFollowUpStatuses(GenericDBHandler.getInstance());
		FollowUpStatus defaultFollowUpStatus = followUpStatuses
				.stream()
				.filter(s -> "NOT_NEEDED".equals(s.getName()))
				.findFirst().orElseThrow(IllegalArgumentException::new);
		String sql = linkSearchDBHandler.buildSearchString(searchTerm, isUrl, isTitle, isDescription, category);

		return linkSearchDBHandler.findAllMatchingLinks(GenericDBHandler.getInstance(), sql, category, defaultFollowUpStatus);
	}
}
