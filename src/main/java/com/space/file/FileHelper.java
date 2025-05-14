package com.space.file;

final public class FileHelper {
    private FileHelper() {}

    public static boolean validateFilepath(String filepath) {

        if (filepath == null || filepath.isEmpty()) {
            return false;
        }

        return !filepath.contains("___");
    }
}
