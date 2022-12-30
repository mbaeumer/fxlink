package se.mbaeumer.fxlink.util;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

/**
 * Created by martinbaumer on 26/06/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DatabaseCheckUtilImpl.class)
public class DatabaseSystemCheckUtilTest extends TestCase {
    @Test
    public void shouldReturnOkWhenDatabaseFolderExists() throws Exception{
        DatabaseCheckUtil databaseCheckUtil = new DatabaseCheckUtilImpl();
        mockDirectoryExists();
        assertEquals(DatabaseCheckResult.OK, databaseCheckUtil.checkDatabaseFolder());
    }

    @Test
    public void shouldReturnFolderMissingWhenDatabaseFolderNotExists() throws Exception{
        DatabaseCheckUtil databaseCheckUtil = new DatabaseCheckUtilImpl();
        mockDirectoryDoesNotExist();
        assertEquals(DatabaseCheckResult.DATABASE_FOLDER_MISSING, databaseCheckUtil.checkDatabaseFolder());
    }

    @Test
    public void shouldReturnOkWhenDatabaseFilesExist() throws Exception{
        DatabaseCheckUtil databaseCheckUtil = new DatabaseCheckUtilImpl();
        mockFilesExist();
        assertEquals(DatabaseCheckResult.OK, databaseCheckUtil.checkDatabaseFiles());
    }

    private void mockFilesExist() throws Exception {
        File mockedDatabasePropertiesFile = Mockito.mock(File.class);
        File mockedDatabaseScriptFile = Mockito.mock(File.class);
        PowerMockito.whenNew(File.class).withParameterTypes(String.class).withArguments("db/fxlink.script").thenReturn(mockedDatabaseScriptFile);
        PowerMockito.whenNew(File.class).withParameterTypes(String.class).withArguments("db/fxlink.properties").thenReturn(mockedDatabasePropertiesFile);
        Mockito.when(mockedDatabaseScriptFile.exists()).thenReturn(true);
        Mockito.when(mockedDatabasePropertiesFile.exists()).thenReturn(true);
    }

    private void mockDirectoryExists() throws Exception {
        File mockedDatabaseFolder = Mockito.mock(File.class);
        PowerMockito.whenNew(File.class).withParameterTypes(String.class).withArguments("db").thenReturn(mockedDatabaseFolder);
        Mockito.when(mockedDatabaseFolder.exists()).thenReturn(true);
        Mockito.when(mockedDatabaseFolder.isDirectory()).thenReturn(true);
    }

    private void mockDirectoryDoesNotExist() throws Exception {
        File mockedDatabaseFolder = Mockito.mock(File.class);
        PowerMockito.whenNew(File.class).withParameterTypes(String.class).withArguments("db").thenReturn(mockedDatabaseFolder);
        Mockito.when(mockedDatabaseFolder.exists()).thenReturn(false);
        Mockito.when(mockedDatabaseFolder.isDirectory()).thenReturn(false);
    }

}
