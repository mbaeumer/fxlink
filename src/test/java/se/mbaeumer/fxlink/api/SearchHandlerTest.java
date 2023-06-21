package se.mbaeumer.fxlink.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.FollowUpStatusReadDBHandler;
import se.mbaeumer.fxlink.handlers.LinkSearchDBHandler;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.FollowUpStatus;
import se.mbaeumer.fxlink.models.Link;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class SearchHandlerTest {

    private SearchHandler searchHandler;

    @Mock
    private LinkSearchDBHandler linkSearchDBHandler;
    @Mock
    private FollowUpStatusReadDBHandler followUpStatusReadDBHandler;

    @Before
    public void setUp(){
        searchHandler = new SearchHandler(linkSearchDBHandler, followUpStatusReadDBHandler);
    }

    @Test
    public void findLinks() throws SQLException {
        Category category = new Category();
        category.setId(-1);
        Mockito.when(followUpStatusReadDBHandler.getFollowUpStatuses(any())).thenReturn(createStatuses());
        Mockito.when(linkSearchDBHandler.buildSearchString("searchterm", true, true, true, category)).thenReturn("sql-string");
        Mockito.when(linkSearchDBHandler.findAllMatchingLinks(any(), any(), any(), any())).thenReturn(createLinks());

        List<Link> actual = searchHandler.findLinks("searchterm", true, true, true, category);
        assertEquals(1, actual.size());
    }

    private List<Link> createLinks(){
        return List.of(createLink());
    }

    private Link createLink(){
        return new Link("title", "www.test.com", "some-description");
    }

    private List<FollowUpStatus> createStatuses(){
        FollowUpStatus statusNotNeeded = new FollowUpStatus();
        statusNotNeeded.setName("NOT_NEEDED");
        FollowUpStatus statusNeeded = new FollowUpStatus();
        statusNeeded.setName("NEEDED");
        return List.of(statusNotNeeded, statusNeeded);
    }
}