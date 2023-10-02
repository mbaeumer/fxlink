package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.FollowUpStatusReadDBHandler;
import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.handlers.LinkUpdateDBHandler;
import se.mbaeumer.fxlink.models.FollowUpOption;
import se.mbaeumer.fxlink.models.FollowUpStatus;
import se.mbaeumer.fxlink.models.Link;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FollowUpRankHandler {
    private final LinkReadDBHandler linkReadDBHandler;
    private final LinkUpdateDBHandler linkUpdateDBHandler;

    private final FollowUpStatusReadDBHandler followUpStatusReadDBHandler;

    private int lowestRank;
    private int highestRank;
    List<Link> linksOrderedByRank;

    private List<FollowUpStatus> followUpStatuses;

    public FollowUpRankHandler(LinkReadDBHandler linkReadDBHandler, LinkUpdateDBHandler linkUpdateDBHandler, int currentRank, FollowUpStatusReadDBHandler followUpStatusReadDBHandler) {
        this.linkReadDBHandler = linkReadDBHandler;
        this.linkUpdateDBHandler = linkUpdateDBHandler;
        this.followUpStatusReadDBHandler = followUpStatusReadDBHandler;
        followUpStatuses = followUpStatusReadDBHandler.getFollowUpStatuses(GenericDBHandler.getInstance());
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

    public void validateFollowUpData(Link oldLink, Link newLink) throws IllegalArgumentException{
        if (!oldLink.getFollowUpStatus().equals(newLink.getFollowUpStatus())
                && "NEEDED".equals(newLink.getFollowUpStatus().getName())){
            if (newLink.getFollowUpRank() == -1){
                throw new IllegalArgumentException();
            }
        }else if (!oldLink.getFollowUpStatus().equals(newLink.getFollowUpStatus())
                && "NOT_NEEDED".equals(newLink.getFollowUpStatus().getName())){
            newLink.setFollowUpRank(-1);
        }else if (!oldLink.getFollowUpStatus().equals(newLink.getFollowUpStatus())
                && "FOLLOWED_UP".equals(newLink.getFollowUpStatus().getName())){
            newLink.setFollowUpRank(-1);
        }
    }

    public void updateRanks(final Link link, final int oldRank){
        /*
        TODO: Change to remove the printStackTrace further down
         */
        if ("FOLLOWED_UP".equals(link.getFollowUpStatus().getName())){
            if (oldRank > 0) {
                linksOrderedByRank.remove(oldRank - 1);
            }
        }else if (isStillRanked(link, oldRank)){
            if (oldRank > 0) {
                linksOrderedByRank.remove(oldRank - 1);
            }
            linksOrderedByRank.add(link.getFollowUpRank()-1, link);
        }else if (isUnranked(link, oldRank)){
            linksOrderedByRank.remove(oldRank - 1);
            link.setFollowUpStatus(getFollowUpStatus("NOT_NEEDED"));
            try {
                linkUpdateDBHandler.updateRank(link, -1, GenericDBHandler.getInstance());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if (oldRank == link.getFollowUpRank()
                && link.getFollowUpRank() > 0
                && "NOT_NEEDED".equals(link.getFollowUpStatus().getName())){
            link.setFollowUpStatus(getFollowUpStatus("NEEDED"));
            try {
                linkUpdateDBHandler.updateRank(link, link.getFollowUpRank(), GenericDBHandler.getInstance());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            return;
        }

        int rank = 1;
        for (Link aLink : linksOrderedByRank){
            aLink.setFollowUpStatus(getFollowUpStatus("NEEDED"));
            try {
                linkUpdateDBHandler.updateRank(aLink, rank, GenericDBHandler.getInstance());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rank++;
        }
    }

    private static boolean isUnranked(Link link, int oldRank) {
        return oldRank != link.getFollowUpRank() && link.getFollowUpRank() == -1;
    }

    private static boolean isStillRanked(Link link, int oldRank) {
        return oldRank != link.getFollowUpRank() && link.getFollowUpRank() >= 1;
    }
    public void setHighestRank(final Link link) throws SQLException{
        link.setFollowUpStatus(getFollowUpStatus("NEEDED"));
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
            updateRanks();
        }
    }
    public void setLowestRank(final Link link) throws SQLException{
        link.setFollowUpStatus(getFollowUpStatus("NEEDED"));
        if (linksOrderedByRank.size() == 0){
            link.setFollowUpRank(1);
            linksOrderedByRank.add(link);
            linkUpdateDBHandler.updateRank(link, link.getFollowUpRank(), GenericDBHandler.getInstance());
        }else{
            int currentRank = link.getFollowUpRank();
            if (currentRank > 0){
                linksOrderedByRank.remove(currentRank - 1);
            }
            linksOrderedByRank.add(link);
            updateRanks();
        }
    }
    public BigDecimal getLowestPossibleRank() throws SQLException {
        linksOrderedByRank = linkReadDBHandler.getLinksOrderedByRank(GenericDBHandler.getInstance());
        return BigDecimal.valueOf(linksOrderedByRank.size());
    }


    private void updateRanks() {
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
    private FollowUpStatus getFollowUpStatus(final String statusName){
        return followUpStatuses
                .stream()
                .filter(s -> statusName.equals(s.getName()))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
