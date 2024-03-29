package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.models.ImportItem;

import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

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

        Assert.assertEquals(3, names.size());
        Assert.assertEquals("import1.txt",
                names.get(0).getFilename());
    }

    @Test
    public void testGetFilename(){
        importItemReadDBHandler = new ImportItemReadDBHandler();

        String actual = importItemReadDBHandler.getFilename("/Users/martinbaumer/Documents/links/work-mac/test.txt");
        Assert.assertEquals("test.txt",actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgument(){
        importItemReadDBHandler = new ImportItemReadDBHandler();
        importItemReadDBHandler.getFilename(null);
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
