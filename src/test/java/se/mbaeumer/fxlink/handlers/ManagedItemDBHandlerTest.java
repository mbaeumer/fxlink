package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.mbaeumer.fxlink.api.ManagedItemHandler;

import java.sql.*;
import java.util.List;

import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class ManagedItemDBHandlerTest {
    private ManagedItemDBOperationHandler classUnderTest;

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
        classUnderTest = new ManagedItemDBHandler();
        String sql = classUnderTest.constructSqlString(null);
        Assert.assertTrue("", sql.equalsIgnoreCase( "select name from ManagedItem"));
    }

    @Test
    public void testGetManagedItems() throws SQLException{
        classUnderTest = new ManagedItemDBHandler();
        String sql = "select name from ManagedItem";

        Mockito.when(databaseConnectionHandler.getConnection()).thenReturn(mockConn);
        Mockito.when(mockConn.createStatement()).thenReturn(mockStatement);
        Mockito.when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
        Mockito.when(mockResultSet.getString("name")).thenReturn("Links", "Categories", "Tags");
        List<String> names = classUnderTest.getAllManagedItems(sql, databaseConnectionHandler);

        Assert.assertTrue("", names.size() == 3);
    }
}
