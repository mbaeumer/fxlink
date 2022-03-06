package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public Map<String, Long> getCategoryCounts(){
		List<Link> linksWithCategory = this.linkReadDBHandler.getAllLinksWithCategory(GenericDBHandler.getInstance());
		List<String> categoryNames = linksWithCategory.stream()
				.map(link -> link.getCategory().getName()).collect(Collectors.toList());

		Map<String, Long> categoryCounts = categoryNames.stream().collect(Collectors.groupingBy(c -> c, Collectors.counting()));

		Long linkCountWithoutCategory = this.linkReadDBHandler.getAllLinksWithNoCategory(GenericDBHandler.getInstance()).stream().count();
		categoryCounts.put("N/A", linkCountWithoutCategory);

		return categoryCounts;
	}

	public Map<Object, Long> getWeekdayCount(){
		List<Link> allLinks = this.linkReadDBHandler.getAllLinks(GenericDBHandler.getInstance());
		List<Date> dates = allLinks.stream().map(link -> link.getCreated()).collect(Collectors.toList());
		Map<Object, Long> weekDayCount = dates.stream()
				.map(date -> getWeekDayOfDate(date))
				.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		return weekDayCount;
	}

	public Map<Object, Long> getHourCount(){
		List<Link> allLinks = this.linkReadDBHandler.getAllLinks(GenericDBHandler.getInstance());
		List<Date> dates = allLinks.stream().map(link -> link.getLastUpdated()).collect(Collectors.toList());

		List<Integer> hours = dates.stream()
				.map(date -> getHourOfDate(date))
				.collect(Collectors.toList());
		return hours.stream()
				.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
	}

	private int getWeekDayOfDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	private int getHourOfDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
}