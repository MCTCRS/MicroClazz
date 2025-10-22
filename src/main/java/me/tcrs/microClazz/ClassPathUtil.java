package me.tcrs.microClazz;

import org.bukkit.Bukkit;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathUtil {

    public static String buildClassPath() {
        try {
            ClassLoader cl = Bukkit.class.getClassLoader();

            // Make sure it's a URLClassLoader
            if (!(cl instanceof URLClassLoader)) {
                throw new IllegalStateException("ClassLoader is not a URLClassLoader!");
            }

            URLClassLoader urlCL = (URLClassLoader) cl;
            URL[] urls = urlCL.getURLs();

            StringBuilder paths = new StringBuilder();
            for (int i = 0; i < urls.length; i++) {
                File file = new File(urls[i].toURI());
                paths.append(file.getAbsolutePath());
                if (i < urls.length - 1) {
                    paths.append(File.pathSeparator);
                }
            }

            return paths.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
