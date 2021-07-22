package se.mbaeumer.fxlink.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;
import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class LinkHandlerTest {
    private LinkHandler linkHandler;

    @Mock
    private LinkReadDBHandler linkReadDBHandler;

    @Test
    public void testGetAllLinks(){
        Mockito.when(linkReadDBHandler.getAllLinksWithCategories(any())).thenReturn(new ArrayList<>());
        linkHandler = new LinkHandler(linkReadDBHandler);
        List<Link> links = linkHandler.getLinks();
        assertTrue(links.size() == 0);
    }

    @Test
    public void testGetLinksByCategory_all(){
        Mockito.when(linkReadDBHandler.getAllLinks(any())).thenReturn(new ArrayList<>());
        linkHandler = new LinkHandler(linkReadDBHandler);
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
        linkHandler = new LinkHandler(linkReadDBHandler);
        Category category = new Category();
        category.setName(ValueConstants.VALUE_N_A);
        List<Link> links = linkHandler.getLinksByCategory(category);
        assertTrue(links.size() == 0);
        Mockito.verify(linkReadDBHandler, Mockito.times(0)).getAllLinksByCategoryId(GenericDBHandler.getInstance(), 0);
        Mockito.verify(linkReadDBHandler, Mockito.times(1)).getAllLinksWithNoCategory(GenericDBHandler.getInstance());
    }
}