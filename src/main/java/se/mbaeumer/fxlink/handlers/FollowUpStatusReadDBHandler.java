package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.FollowUpStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FollowUpStatusReadDBHandler {

    public List<FollowUpStatus> getFollowUpStatuses(GenericDBHandler dbh){
        Connection connection = dbh.getConnection();
        List<FollowUpStatus> followUpStatuses = new ArrayList();

        String sql = "select id, name, description" +
                " from followupstatus fus";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FollowUpStatus followUpStatus = new FollowUpStatus();
                followUpStatus.setId(rs.getInt("id"));
                followUpStatus.setName(rs.getString("name"));
                followUpStatus.setDescription(rs.getString("description"));
                followUpStatuses.add(followUpStatus);
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return followUpStatuses;


    }
}
