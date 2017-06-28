package se.mbaeumer.fxlink.util;

import java.io.File;

/**
 * Created by martinbaumer on 26/06/17.
 */
public class DatabaseCheckUtilImpl implements DatabaseCheckUtil {
    private static final String DB_SCRIPT_FILE  = "fxlink.script";
    private static final String DB_PROPERTIES_FILE  = "fxlink.properties";
    private static final String DB_FOLDER  = "db";

    @Override
    public DatabaseCheckResult checkDatabaseFiles() {
        File databaseScriptFile = new File(DB_FOLDER + "/" + DB_SCRIPT_FILE);
        File databasePropertiesFile = new File(DB_FOLDER + "/" + DB_PROPERTIES_FILE);
        if (!databaseScriptFile.exists()){
            return DatabaseCheckResult.DATABASE_SCRIPT_FILE_MISSING;
        }

        if (!databasePropertiesFile.exists()){
            return DatabaseCheckResult.DATABASE_PROPERTIES_FILE_MISSING;
        }

        return DatabaseCheckResult.OK;
    }

    @Override
    public DatabaseCheckResult checkDatabaseFolder(){
        DatabaseCheckResult result = DatabaseCheckResult.OK;

        File databaseFolder = new File(DB_FOLDER);
        if (!databaseFolder.isDirectory()){
            result = DatabaseCheckResult.DATABASE_FOLDER_MISSING;
        }

        return result;
    }
}
