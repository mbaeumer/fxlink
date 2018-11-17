package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.mbaeumer.fxlink.models.ImportItem;

import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class ImportItemReadDBHandlerTest {
    private ImportItemReadDBHandler importItemReadDBHandler;

    @Mock
    private DatabaseConnectionHandler databaseConnectionHandler;

    @Mock
    Connection mockConn;

    @Mock
    Statement mockStatement;

    @Mock
    ResultSet mockResultSet;

    @Test
    public void testGetImportItems() throws SQLException{
        importItemReadDBHandler = new ImportItemReadDBHandler();
        String sql = "select * from ImportItem";

        mockStuff();

        List<ImportItem> names = importItemReadDBHandler.getAllImportItems(sql,databaseConnectionHandler);

        Assert.assertTrue("there should be three items", names.size() == 3);
        Assert.assertTrue("the filename of the first item should be import1.txt",
                names.get(0).getFilename().equalsIgnoreCase("import1.txt"));
    }

    private void mockStuff() throws SQLException {
        Mockito.when(databaseConnectionHandler.getConnection()).thenReturn(mockConn);
        Mockito.when(mockConn.createStatement()).thenReturn(mockStatement);
        Mockito.when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
        Mockito.when(mockResultSet.getInt("id")).thenReturn(1, 2, 3);
        Mockito.when(mockResultSet.getString("filename")).thenReturn("import1.txt", "import2.txt", "import3.txt");
        Mockito.when(mockResultSet.getTimestamp("created")).thenReturn(new Timestamp(new Date().getTime()),
                new Timestamp(new Date().getTime()), new Timestamp(new Date().getTime()));
    }
}
