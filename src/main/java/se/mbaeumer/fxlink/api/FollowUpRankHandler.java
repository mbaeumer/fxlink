package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.handlers.LinkUpdateDBHandler;
import se.mbaeumer.fxlink.models.Link;

import java.sql.SQLException;
import java.util.List;

public class FollowUpRankHandler {
    private final LinkReadDBHandler linkReadDBHandler;
    private final LinkUpdateDBHandler linkUpdateDBHandler;
    private int lowestRank;
    private int highestRank;
    List<Link> linksOrderedByRank;

    public FollowUpRankHandler(LinkReadDBHandler linkReadDBHandler, LinkUpdateDBHandler linkUpdateDBHandler, int currentRank) {
        this.linkReadDBHandler = linkReadDBHandler;
        this.linkUpdateDBHandler = linkUpdateDBHandler;
        try {
            this.init(currentRank);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLowestRank() {
        return lowestRank;
    }

    public int getHighestRank() {
        return highestRank;
    }

    public List<Link> getLinksOrderedByRank() {
        return linksOrderedByRank;
    }

    public void init(int currentRank) throws SQLException {
        linksOrderedByRank = linkReadDBHandler.getLinksOrderedByRank(GenericDBHandler.getInstance());
        highestRank = 1;
        lowestRank = linksOrderedByRank.size();
        if (currentRank == -1) {
            lowestRank = linksOrderedByRank.size() + 1;
        }

    }

    public void updateRanks(final Link link, final int oldRank){
        if (oldRank != link.getFollowUpRank()){
            if (oldRank > 0) {
                linksOrderedByRank.remove(oldRank - 1);
            }

            linksOrderedByRank.add(link.getFollowUpRank()-1, link);
        }

        int rank = 1;
        for (Link aLink : linksOrderedByRank){
            try {
                linkUpdateDBHandler.updateRank(aLink, rank, GenericDBHandler.getInstance());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rank++;
        }
    }
}
