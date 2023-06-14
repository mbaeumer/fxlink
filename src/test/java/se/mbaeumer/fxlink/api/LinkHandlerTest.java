package se.mbaeumer.fxlink.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.FollowUpStatus;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class LinkHandlerTest {
    private LinkHandler linkHandler;

    @Mock
    private LinkReadDBHandler linkReadDBHandler;

    @Mock
    private LinkTagReadDBHandler linkTagReadDBHandler;

    @Mock
    private LinkCreationDBHandler linkCreationDBHandler;

    @Mock
    private LinkUpdateDBHandler linkUpdateDBHandler;

    @Mock
    private LinkDeletionDBHandler linkDeletionDBHandler;

    @Mock
    private FollowUpStatusReadDBHandler followUpStatusReadDBHandler;

    @Before
    public void setUp(){
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler,
                linkCreationDBHandler, linkUpdateDBHandler, linkDeletionDBHandler, followUpStatusReadDBHandler);
    }

    @Test
    public void testGetAllLinks(){
        Mockito.when(linkReadDBHandler.getAllLinksWithCategories(any(), any())).thenReturn(new ArrayList<>());
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any())).thenReturn(List.of(createFollowUpStatus()));
        List<Link> links = linkHandler.getLinks();
        assertEquals(0, links.size());
    }

    @Test
    public void testGetLinksByCategory_all(){
        Mockito.when(linkReadDBHandler.getAllLinks(any(), any())).thenReturn(new ArrayList<>());
        List<FollowUpStatus> followUpStatuses = createStatuses();
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any())).thenReturn(followUpStatuses);
        Category category = new Category();
        category.setName(ValueConstants.VALUE_ALL);
        List<Link> links = linkHandler.getLinksByCategory(category);
        assertEquals(0,links.size());
        Mockito.verify(linkReadDBHandler, Mockito.times(0)).getAllLinksByCategoryId(GenericDBHandler.getInstance(), 0, createStatuses().get(0));
        Mockito.verify(linkReadDBHandler, Mockito.times(1)).getAllLinks(GenericDBHandler.getInstance(), followUpStatuses.get(0));
    }

    @Test
    public void testGetLinksByCategory_null(){
        Mockito.when(linkReadDBHandler.getAllLinksWithNoCategory(any(), any())).thenReturn(new ArrayList<>());
        List<FollowUpStatus> followUpStatuses = createStatuses();
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any())).thenReturn(followUpStatuses);
        Category category = new Category();
        category.setName(ValueConstants.VALUE_N_A);
        List<Link> links = linkHandler.getLinksByCategory(category);
        assertEquals(0,links.size());
        Mockito.verify(linkReadDBHandler, Mockito.times(0)).getAllLinksByCategoryId(GenericDBHandler.getInstance(), 0, followUpStatuses.get(0));
        Mockito.verify(linkReadDBHandler, Mockito.times(1)).getAllLinksWithNoCategory(GenericDBHandler.getInstance(), followUpStatuses.get(0));
    }

    @Test
    public void testGetLinksWithTag() throws SQLException {
        Mockito.when(linkTagReadDBHandler.getAllLinksByTagId(GenericDBHandler.getInstance(), 0))
                .thenReturn(new ArrayList<>());
        List<Link> links = linkHandler.getLinksWithTag(0);
        assertEquals(0,links.size());
    }

    @Test
    public void testCreateLink_success() throws SQLException {
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any())).thenReturn(createStatuses());
        Mockito.when(linkCreationDBHandler.constructSqlString(link)).thenReturn("insert into something");
        Mockito.when(linkCreationDBHandler.createLink(any(), any())).thenReturn(1);
        try {
            linkHandler.createLink(link);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test(expected = SQLException.class)
    public void testCreateLink_failure() throws SQLException {
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any())).thenReturn(createStatuses());
        Mockito.when(linkCreationDBHandler.constructSqlString(link)).thenReturn("insert into something");
        Mockito.when(linkCreationDBHandler.createLink(any(), any())).thenThrow(SQLException.class);
        linkHandler.createLink(link);
    }

    private List<FollowUpStatus> createStatuses(){
        FollowUpStatus status = new FollowUpStatus();
        status.setName("NOT_NEEDED");
        return List.of(status);
    }

    @Test
    public void testUpdateLink_success() throws SQLException{
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any())).thenReturn(createStatuses());
        Mockito.when(linkUpdateDBHandler.constructSqlString(link)).thenReturn("insert into something");
        Mockito.doNothing().when(linkUpdateDBHandler).updateLink(any(), any());
        linkHandler.updateLink(link);
    }

    @Test(expected = SQLException.class)
    public void testUpdateLink_failure() throws SQLException{
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any())).thenReturn(createStatuses());
        Mockito.when(linkUpdateDBHandler.constructSqlString(link)).thenReturn("insert into something");
        Mockito.doThrow(SQLException.class).when(linkUpdateDBHandler).updateLink(any(), any());
        linkHandler.updateLink(link);
    }

    @Test
    public void deleteLink() throws SQLException {
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
        link.setId(1);
        Mockito.when(linkDeletionDBHandler.constructSqlString(link)).thenReturn("delete from link");
        Mockito.doNothing().when(linkDeletionDBHandler).deleteLink(any(), any());
        linkHandler.deleteLink(link);
    }

    @Test
    public void getWeekDayCount(){
        Mockito.when(linkReadDBHandler.getAllLinks(any(), any()))
                .thenReturn(createListOfLinksWithDifferentCreationDates());
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any()))
                .thenReturn(createStatuses());
        Map<Object, Long> weekDayCounts = linkHandler.getWeekdayCount();
        Long actualCount = weekDayCounts.get(1);
        assertEquals(2, actualCount.longValue());
    }

    private List<Link> createListOfLinksWithDifferentCreationDates(){
        List<Link> links = new ArrayList<>();
        Link link = createLink("www.gp.se");
        link.setCreated(createCreationDate(20));
        links.add(link);
        link = createLink( "www.spiegel.de");
        link.setCreated(createCreationDate(20));
        links.add(link);
        link = createLink( "www.kicker.de");
        link.setCreated(createCreationDate(23));
        links.add(link);
        link = createLink( "www.stackoverflow.blog");
        link.setCreated(createCreationDate(26));
        links.add(link);

        return links;

    }

    private Link createLink(String url){
        return new Link("", url, "");
    }

    private Date createCreationDate(int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, 2);
        calendar.set(Calendar.YEAR, 2022);

        return calendar.getTime();
    }

    private List<Link> createListOfLinksWithDifferentTimestamps(){
        List<Link> links = new ArrayList<>();
        Link link = createLink( "www.gp.se");
        link.setLastUpdated(createTime(22));
        links.add(link);
        link = createLink( "www.spiegel.de");
        link.setLastUpdated(createTime(22));
        links.add(link);
        link = createLink( "www.kicker.de");
        link.setLastUpdated(createTime(13));
        links.add(link);
        link = createLink( "www.stackoverflow.blog");
        link.setLastUpdated(createTime(19));
        links.add(link);

        return links;

    }


    private Date createTime(int hour){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);

        return calendar.getTime();
    }

    @Test
    public void getHourCount() {
        Mockito.when(linkReadDBHandler.getAllLinks(any(), any()))
                .thenReturn(createListOfLinksWithDifferentTimestamps());
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any()))
                .thenReturn(createStatuses());

        Map<Object, Long> hourCount = linkHandler.getHourCount();
        Assert.assertEquals(3, hourCount.entrySet().size());
        Long aLong = hourCount.get(22);
        Assert.assertEquals(2, aLong.longValue());
    }

    private FollowUpStatus createFollowUpStatus(){
        FollowUpStatus followUpStatus = new FollowUpStatus();
        followUpStatus.setId(1);
        followUpStatus.setName("NOT_NEEDED");

        return followUpStatus;
    }
}