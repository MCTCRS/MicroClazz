package me.tcrs.microClazz;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class filePathList {

    public static List<String> listAllFiles(File baseFolder) {
        List<String> filesList = new ArrayList<>();
        Path basePath = baseFolder.toPath();
        listFilesRecursive(baseFolder, basePath, filesList);
        return filesList;
    }

    private static void listFilesRecursive(File folder, Path basePath, List<String> list) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                listFilesRecursive(f, basePath, list);
            } else {
                String relative = basePath.relativize(f.toPath()).toString().replace("\\", "/");
                list.add(relative);
            }
        }
    }

}
