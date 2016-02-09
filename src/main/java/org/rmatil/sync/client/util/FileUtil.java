package org.rmatil.sync.client.util;

import java.io.File;

public class FileUtil {

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
}
