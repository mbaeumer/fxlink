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
}
