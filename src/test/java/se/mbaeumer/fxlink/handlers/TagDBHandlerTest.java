package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class TagDBHandlerTest {

    private TagDBOperationHandler classUnderTest;

    @Mock
    private DatabaseConnectionHandler databaseConnectionHandler;

    @Mock
    Connection mockConn;

    @Mock
    Statement mockStatement;

    @Mock
    ResultSet mockResultSet;

    @Test
    public void testConstructSql(){
        classUnderTest = new TagDBHandler();
        String expected = "select t.id as tagId, t.name, t.description, t.created, t.lastUpdated " +
                " from tag t";
        String sql = classUnderTest.constructSqlString(null);
        Assert.assertTrue("", sql.equalsIgnoreCase( expected));
    }

    @Test
    public void testGetAlltags() throws SQLException {
        classUnderTest = new TagDBHandler();
        String sql = "select t.id as tagId, t.name, t.description, t.created, t.lastUpdated " +
                " from tag t";

        Mockito.when(databaseConnectionHandler.getConnection()).thenReturn(mockConn);
        Mockito.when(mockConn.createStatement()).thenReturn(mockStatement);
        Mockito.when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);

        Mockito.when(mockResultSet.getInt("tagId")).thenReturn(1);
        Mockito.when(mockResultSet.getString("name")).thenReturn("sample");
        Mockito.when(mockResultSet.getString("description")).thenReturn("a sample tag");
        List<Tag> tags = classUnderTest.getAllTags(sql, databaseConnectionHandler);

        Assert.assertEquals(1, tags.size());
        Tag tag = tags.get(0);
        Assert.assertEquals(1, tag.getId());
        Assert.assertTrue(tag.getName().equalsIgnoreCase("sample"));
        Assert.assertTrue(tag.getDescription().equalsIgnoreCase("a sample tag"));
    }
}
