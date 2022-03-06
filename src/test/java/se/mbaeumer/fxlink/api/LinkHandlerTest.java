package se.mbaeumer.fxlink.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.SQLException;
import java.text.ParseException;
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

    @Before
    public void setUp(){
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler,
                linkCreationDBHandler, linkUpdateDBHandler, linkDeletionDBHandler);
    }

    @Test
    public void testGetAllLinks(){
        Mockito.when(linkReadDBHandler.getAllLinksWithCategories(any())).thenReturn(new ArrayList<>());
        List<Link> links = linkHandler.getLinks();
        assertTrue(links.size() == 0);
    }

    @Test
    public void testGetLinksByCategory_all(){
        Mockito.when(linkReadDBHandler.getAllLinks(any())).thenReturn(new ArrayList<>());
        Category category = new Category();
        category.setName(ValueConstants.VALUE_ALL);
        List<Link> links = linkHandler.getLinksByCategory(category);
        assertTrue(links.size() == 0);
        Mockito.verify(linkReadDBHandler, Mockito.times(0)).getAllLinksByCategoryId(GenericDBHandler.getInstance(), 0);
        Mockito.verify(linkReadDBHandler, Mockito.times(1)).getAllLinks(GenericDBHandler.getInstance());
    }

    @Test
    public void testGetLinksByCategory_null(){
        Mockito.when(linkReadDBHandler.getAllLinksWithNoCategory(any())).thenReturn(new ArrayList<>());
        Category category = new Category();
        category.setName(ValueConstants.VALUE_N_A);
        List<Link> links = linkHandler.getLinksByCategory(category);
        assertTrue(links.size() == 0);
        Mockito.verify(linkReadDBHandler, Mockito.times(0)).getAllLinksByCategoryId(GenericDBHandler.getInstance(), 0);
        Mockito.verify(linkReadDBHandler, Mockito.times(1)).getAllLinksWithNoCategory(GenericDBHandler.getInstance());
    }

    @Test
    public void testGetLinksWithTag() throws SQLException {
        Mockito.when(linkTagReadDBHandler.getAllLinksByTagId(GenericDBHandler.getInstance(), 0))
                .thenReturn(new ArrayList<>());
        List<Link> links = linkHandler.getLinksWithTag(0);
        assertTrue(links.size() == 0);
    }

    @Test
    public void testCreateLink_success() throws SQLException {
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
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
        Mockito.when(linkCreationDBHandler.constructSqlString(link)).thenReturn("insert into something");
        Mockito.when(linkCreationDBHandler.createLink(any(), any())).thenThrow(SQLException.class);
        linkHandler.createLink(link);
    }

    @Test
    public void testUpdateLink_success() throws SQLException, ParseException{
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
        Mockito.when(linkUpdateDBHandler.constructSqlString(link)).thenReturn("insert into something");
        Mockito.doNothing().when(linkUpdateDBHandler).updateLink(any(), any());
        linkHandler.updateLink(link);
    }

    @Test(expected = SQLException.class)
    public void testUpdateLink_failure() throws SQLException, ParseException {
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
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
        Mockito.when(linkReadDBHandler.getAllLinks(any()))
                .thenReturn(createListOfLinksWithDifferentCreationDates());
        Map<Object, Long> weekDayCounts = linkHandler.getWeekdayCount();
        Long actualCount = weekDayCounts.get(1);
        assertEquals(2, actualCount.longValue());
    }

    private List<Link> createListOfLinksWithDifferentCreationDates(){
        List<Link> links = new ArrayList<>();
        Link link = createLink("", "www.gp.se", "");
        link.setCreated(createCreationDate(20, 2, 2022));
        links.add(link);
        link = createLink("", "www.spiegel.de", "");
        link.setCreated(createCreationDate(20, 2, 2022));
        links.add(link);
        link = createLink("", "www.kicker.de", "");
        link.setCreated(createCreationDate(23, 2, 2022));
        links.add(link);
        link = createLink("", "www.stackoverflow.blog", "");
        link.setCreated(createCreationDate(26, 2, 2022));
        links.add(link);

        return links;

    }

    private Link createLink(String title, String url, String description){
        return new Link(title, url, description);
    }

    private Date createCreationDate(int day, int month, int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        return calendar.getTime();

    }
}