package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TagDBHandler implements TagDBOperationHandler {
    @Override
    public List<Tag> getAllTags(String sql, DatabaseConnectionHandler databaseConnectionHandler) {
        Connection connection = databaseConnectionHandler.getConnection();
        List<Tag> tags = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Tag tag = new Tag();
                tag.setId(rs.getInt("tagId"));
                tag.setName(rs.getString("name"));
                tag.setDescription(rs.getString("description"));
                tag.setCreated(rs.getTimestamp("created"));
                tag.setLastUpdated(rs.getTimestamp("lastUpdated"));
                tags.add(tag);
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    @Override
    public String constructSqlString(Object object) {
        return "select t.id as tagId, t.name, t.description, t.created, t.lastUpdated " +
                " from tag t";
    }
}
