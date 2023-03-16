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
        if (oldRank != link.getFollowUpRank() && link.getFollowUpRank() >= 1){
            if (oldRank > 0) {
                linksOrderedByRank.remove(oldRank - 1);
            }

            linksOrderedByRank.add(link.getFollowUpRank()-1, link);
        }else if (oldRank != link.getFollowUpRank() && link.getFollowUpRank() == -1){
            linksOrderedByRank.remove(oldRank - 1);
            try {
                linkUpdateDBHandler.updateRank(link, -1, GenericDBHandler.getInstance());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if (oldRank == link.getFollowUpRank()){
            return;
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

    public void setHighestRank(final Link link) throws SQLException{
        if (linksOrderedByRank.size() == 0){
            link.setFollowUpRank(1);
            linksOrderedByRank.add(link);
            linkUpdateDBHandler.updateRank(link, link.getFollowUpRank(), GenericDBHandler.getInstance());
        }else{
            int currentRank = link.getFollowUpRank();
            if (currentRank > 0){
                linksOrderedByRank.remove(currentRank-1);
            }
            linksOrderedByRank.add(0, link);
            updateRanks(link);
        }
    }

    public void setLowestRank(final Link link) throws SQLException{
        if (linksOrderedByRank.size() == 0){
            link.setFollowUpRank(1);
            linksOrderedByRank.add(link);
            linkUpdateDBHandler.updateRank(link, link.getFollowUpRank(), GenericDBHandler.getInstance());
        }else{
            int currentRank = link.getFollowUpRank();
            if (currentRank > 0){
                linksOrderedByRank.remove(currentRank - 1);
            }
            //link.setFollowUpRank(linksOrderedByRank.size());
            linksOrderedByRank.add(link);
            updateRanks(link);
        }

    }
    private void updateRanks(Link link) {
        int rank = 1;
        for (Link aLink : linksOrderedByRank){
            try {
                aLink.setFollowUpRank(rank);
                linkUpdateDBHandler.updateRank(aLink, rank, GenericDBHandler.getInstance());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rank++;
        }
    }
}
