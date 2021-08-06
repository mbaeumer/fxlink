package se.mbaeumer.fxlink.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testGetAllLinks(){
        Mockito.when(linkReadDBHandler.getAllLinksWithCategories(any())).thenReturn(new ArrayList<>());
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler, linkCreationDBHandler, linkUpdateDBHandler);
        List<Link> links = linkHandler.getLinks();
        assertTrue(links.size() == 0);
    }

    @Test
    public void testGetLinksByCategory_all(){
        Mockito.when(linkReadDBHandler.getAllLinks(any())).thenReturn(new ArrayList<>());
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler, linkCreationDBHandler, linkUpdateDBHandler);
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
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler, linkCreationDBHandler, linkUpdateDBHandler);
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
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler, linkCreationDBHandler, linkUpdateDBHandler);
        List<Link> links = linkHandler.getLinksWithTag(0);
        assertTrue(links.size() == 0);
    }

    @Test
    public void testCreateLink_success() throws SQLException {
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler, linkCreationDBHandler, linkUpdateDBHandler);
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
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler, linkCreationDBHandler, linkUpdateDBHandler);
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
        Mockito.when(linkCreationDBHandler.constructSqlString(link)).thenReturn("insert into something");
        Mockito.when(linkCreationDBHandler.createLink(any(), any())).thenThrow(SQLException.class);
        linkHandler.createLink(link);
    }

    @Test
    public void testUpdateLink_success() throws SQLException, ParseException{
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler, linkCreationDBHandler, linkUpdateDBHandler);
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
        Mockito.when(linkUpdateDBHandler.constructSqlString(link)).thenReturn("insert into something");
        Mockito.doNothing().when(linkUpdateDBHandler).updateLink(any(), any());
        linkHandler.updateLink(link);
    }

    @Test(expected = SQLException.class)
    public void testUpdateLink_failure() throws SQLException, ParseException {
        linkHandler = new LinkHandler(linkReadDBHandler, linkTagReadDBHandler, linkCreationDBHandler, linkUpdateDBHandler);
        Link link = new Link("", "www.spiegel.de", "Der Spiegel");
        Mockito.when(linkUpdateDBHandler.constructSqlString(link)).thenReturn("insert into something");
        Mockito.doThrow(SQLException.class).when(linkUpdateDBHandler).updateLink(any(), any());
        linkHandler.updateLink(link);
    }
}