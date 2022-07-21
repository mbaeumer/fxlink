package se.mbaeumer.fxlink.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.handlers.LinkUpdateDBHandler;
import se.mbaeumer.fxlink.models.Link;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class FollowUpRankHandlerTest {
    private FollowUpRankHandler followUpRankHandler;
    @Mock
    private LinkReadDBHandler linkReadDBHandler;
    @Mock
    private LinkUpdateDBHandler linkUpdateDBHandler;

    @Test
    public void initRanksWhenNoLinkRanked() throws SQLException {
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(new ArrayList<>());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, -1);
        Assert.assertEquals(1, followUpRankHandler.getHighestRank());
        Assert.assertEquals(1, followUpRankHandler.getLowestRank());
    }

    @Test
    public void initRanksWhenTwoLinksRankedCurrentLinkNotRanked() throws SQLException {
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(createRankedLinks());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, -1);

        Assert.assertEquals(1, followUpRankHandler.getHighestRank());
        Assert.assertEquals(3, followUpRankHandler.getLowestRank());
    }

    @Test
    public void initRanksWhenTwoLinksRankedCurrentLinkRanked() throws SQLException {
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(createRankedLinks());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, 1);
        Assert.assertEquals(1, followUpRankHandler.getHighestRank());
        Assert.assertEquals(2, followUpRankHandler.getLowestRank());
    }

    private List<Link> createRankedLinks(){
        List<Link> links = new ArrayList<>();

        Link link = new Link("test", "www.test1.com", "");
        link.setFollowUpRank(1);
        links.add(link);

        link = new Link("test2", "www.test2.com", "");
        link.setFollowUpRank(2);
        links.add(link);

        return links;
    }
}