package se.mbaeumer.fxlink.util;

import java.io.File;

/**
 * Created by martinbaumer on 25/06/17.
 */
public class DatabaseSystemCheckUtil {
    public static boolean databaseFolderExists(){
        String folder = "db";
        File databaseFolder = new File(folder);
        return databaseFolder.isDirectory();
    }

    public static DatabaseCheckResult checkDatabase(){
        DatabaseCheckResult result = DatabaseCheckResult.OK;

        String folder = "db";
        File databaseFolder = new File(folder);
        File databaseFile = new File(folder + "/" + "fxlink.script");
        if (!databaseFolder.isDirectory()){
            result = DatabaseCheckResult.DATABASE_FOLDER_MISSING;
        }else if (!databaseFile.exists()){
            result = DatabaseCheckResult.DATABASE_SCRIPT_FILE_MISSING;
        }

        return result;
    }
}
