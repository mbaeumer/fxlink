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

import static org.mockito.ArgumentMatchers.*;

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

    @Test
    public void testUpdateRanksHigherRank() throws SQLException {
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(createRankedLinks());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, 1);

        Link link = new Link("test", "www.test2.com", "");
        link.setFollowUpRank(1);
        followUpRankHandler.updateRanks(link, 2);
        List<Link> linksOrderedByRank = followUpRankHandler.getLinksOrderedByRank();
        Link firstLink = linksOrderedByRank.get(0);
        Assert.assertEquals("www.test2.com",firstLink.getURL());
        Mockito.verify(linkUpdateDBHandler, Mockito.times(2)).updateRank(any(), anyInt(), any());
    }

    @Test
    public void testUnrankFirst() throws SQLException {
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(createRankedLinks());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, 1);

        Link link = new Link("test", "www.test1.com", "");
        link.setFollowUpRank(-1);
        followUpRankHandler.updateRanks(link, 1);

        List<Link> linksOrderedByRank = followUpRankHandler.getLinksOrderedByRank();
        Assert.assertEquals(linksOrderedByRank.size(), 1);

        Mockito.verify(linkUpdateDBHandler, Mockito.times(2)).updateRank(any(), anyInt(), any());
    }

    @Test
    public void testRankUnchanged() throws SQLException {
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(createRankedLinks());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, 1);

        Link link = new Link("test", "www.test1.com", "");
        link.setFollowUpRank(1);
        followUpRankHandler.updateRanks(link, 1);

        List<Link> linksOrderedByRank = followUpRankHandler.getLinksOrderedByRank();
        Assert.assertEquals(linksOrderedByRank.size(), 2);

        Mockito.verify(linkUpdateDBHandler, Mockito.never()).updateRank(any(), anyInt(), any());
    }

    @Test
    public void setHighestRank_existingRanks() throws SQLException{
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(createRankedLinks());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, -1);

        Link link = new Link("test2", "www.newlink.com", "");
        link.setFollowUpRank(-1);
        followUpRankHandler.setHighestRank(link);
        List<Link> linksOrderedByRank = followUpRankHandler.getLinksOrderedByRank();
        Link highestLink = linksOrderedByRank.get(0);
        Assert.assertEquals(linksOrderedByRank.size(), 3);
        Assert.assertEquals("www.newlink.com", highestLink.getURL());
        Assert.assertEquals(1, highestLink.getFollowUpRank());
        Mockito.verify(linkUpdateDBHandler, Mockito.times(3)).updateRank(any(), anyInt(), any());

    }

    @Test
    public void setHighestRank_existingRanks_switch() throws SQLException{
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(createRankedLinks());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, -1);

        Link link = new Link("test2", "www.test2.com", "");
        link.setFollowUpRank(2);
        followUpRankHandler.setHighestRank(link);
        List<Link> linksOrderedByRank = followUpRankHandler.getLinksOrderedByRank();
        Link highestLink = linksOrderedByRank.get(0);
        Assert.assertEquals(linksOrderedByRank.size(), 2);
        Assert.assertEquals("www.test2.com", highestLink.getURL());
        Assert.assertEquals(1, highestLink.getFollowUpRank());
        Mockito.verify(linkUpdateDBHandler, Mockito.times(2)).updateRank(any(), anyInt(), any());

    }
    @Test
    public void setHighestRank_noRanks() throws SQLException{
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(new ArrayList<>());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, -1);

        Link link = new Link("test", "www.newlink.com", "");
        link.setFollowUpRank(-1);
        followUpRankHandler.setHighestRank(link);
        List<Link> linksOrderedByRank = followUpRankHandler.getLinksOrderedByRank();
        Link highestLink = linksOrderedByRank.get(0);
        Assert.assertEquals(linksOrderedByRank.size(), 1);
        Assert.assertEquals("www.newlink.com", highestLink.getURL());
        Assert.assertEquals(1, highestLink.getFollowUpRank());
        Mockito.verify(linkUpdateDBHandler, Mockito.times(1)).updateRank(any(), anyInt(), any());
    }

    @Test
    public void setLowestRank_noRanks() throws SQLException {
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(new ArrayList<>());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, -1);

        Link link = new Link("test", "www.newlink.com", "");
        link.setFollowUpRank(-1);
        followUpRankHandler.setLowestRank(link);
        List<Link> linksOrderedByRank = followUpRankHandler.getLinksOrderedByRank();
        Link highestLink = linksOrderedByRank.get(0);
        Assert.assertEquals(linksOrderedByRank.size(), 1);
        Assert.assertEquals("www.newlink.com", highestLink.getURL());
        Assert.assertEquals(1, highestLink.getFollowUpRank());
        Mockito.verify(linkUpdateDBHandler, Mockito.times(1)).updateRank(any(), anyInt(), any());
    }

    @Test
    public void setLowestRank_existingRanks() throws SQLException{
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(createRankedLinks());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, -1);

        Link link = new Link("test2", "www.newlink.com", "");
        link.setFollowUpRank(-1);
        followUpRankHandler.setLowestRank(link);
        List<Link> linksOrderedByRank = followUpRankHandler.getLinksOrderedByRank();
        Link lowestLink = linksOrderedByRank.get(linksOrderedByRank.size() - 1);
        Assert.assertEquals(linksOrderedByRank.size(), 3);
        Assert.assertEquals("www.newlink.com", lowestLink.getURL());
        Assert.assertEquals(3, lowestLink.getFollowUpRank());
        Mockito.verify(linkUpdateDBHandler, Mockito.times(3)).updateRank(any(), anyInt(), any());

    }

    @Test
    public void setLowestRank_existingRanks_switch() throws SQLException{
        Mockito.when(linkReadDBHandler.getLinksOrderedByRank(any())).thenReturn(createRankedLinks());
        followUpRankHandler = new FollowUpRankHandler(linkReadDBHandler, linkUpdateDBHandler, -1);

        Link link = new Link("test", "www.test1.com", "");
        link.setFollowUpRank(1);
        followUpRankHandler.setLowestRank(link);
        List<Link> linksOrderedByRank = followUpRankHandler.getLinksOrderedByRank();
        Link lowestLink = linksOrderedByRank.get(linksOrderedByRank.size() - 1);
        Assert.assertEquals(2, linksOrderedByRank.size());
        Assert.assertEquals("www.test1.com", lowestLink.getURL());
        Assert.assertEquals(2, lowestLink.getFollowUpRank());
        Mockito.verify(linkUpdateDBHandler, Mockito.times(2)).updateRank(any(), anyInt(), any());
    }
}