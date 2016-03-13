package org.rmatil.sync.client.util;

import java.io.File;

public class FileUtils {

    /**
     * Deletes recursively the given file (if it is a directory)
     * or just removes itself
     *
     * @param file The file or dir to remove
     *
     * @return True if the deletion was successful
     */
    public static boolean delete(File file) {
        if (file.isDirectory()) {
            File[] contents = file.listFiles();

            if (null != contents) {
                for (File child : contents) {
                    delete(child);
                }
            }

            file.delete();

            return true;
        } else {
            return file.delete();
        }
    }

    public static String resolveUserHome(String path) {
        return path.replaceFirst("^~", System.getProperty("user.home"));
    }
}
