package me.tcrs.microClazz;

import org.bukkit.Bukkit;

import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.HandlerList;

import org.bukkit.event.Listener;


public class ScriptManager {

    public static void invokeClass(String keyName, String methodName) throws Exception {
        if (!MicroClazz.loadedClassData.containsKey(keyName))
            throw new Exception("No loaded class with key " + keyName + " found");

        Map<Byte, Object> classobj = MicroClazz.loadedClassData.get(keyName);
        Class<?> cls = (Class<?>) classobj.get(Keys.CLS);
        Object instance = classobj.get(Keys.INSTANCE);

        cls.getMethod(methodName).invoke(instance);
    }




    public static void unloadClass(String keyName) throws Exception {
        if (!MicroClazz.loadedClassData.containsKey(keyName))
            throw new Exception("No loaded class with key " + keyName + " found");

        Map<Byte, Object> classobj = MicroClazz.loadedClassData.get(keyName);
        URLClassLoader loader = (URLClassLoader) classobj.get(Keys.LOADER);
        Class<?> cls = (Class<?>) classobj.get(Keys.CLS);
        Object instance = classobj.get(Keys.INSTANCE);

        try {
            cls.getMethod("onScriptUnLoad").invoke(instance);
        } catch(Exception ignored){}

        // Unregister Bukkit listeners if instance is a Listener
        if (Listener.class.isAssignableFrom(cls)) {
            HandlerList.unregisterAll((Listener) instance);
        }

        MicroClazz.loadedClassData.remove(keyName);
        loader.close();
        classobj.clear();

        System.gc();
    }



    public static String loadClass(File classFilePath, String keyName) throws Exception {

        String retWarn = "";

        if (!classFilePath.getName().endsWith(".class"))
            throw new Exception("Expect a file with '.class' extension.");

        if (!classFilePath.exists())
            throw new Exception("File doesn't exist.");

        if (MicroClazz.loadedClassData.containsKey(keyName)) {
            unloadClass(keyName);
            retWarn = "Key " + keyName + " is already loaded, replacing with new one.";
        }

        URL url = classFilePath.getParentFile().toURI().toURL();
        URLClassLoader loader = URLClassLoader.newInstance(
                new URL[]{url},
                org.bukkit.Bukkit.class.getClassLoader()
        );

        String className = classFilePath.getName().replaceFirst("\\.class$", "");
        Class<?> cls = loader.loadClass(className);
        Object instance = cls.getConstructor().newInstance();

        Map<Byte, Object> result = new HashMap<>();
        result.put(Keys.CLS, cls);
        result.put(Keys.LOADER, loader);

        if (Listener.class.isAssignableFrom(cls))
            Bukkit.getPluginManager().registerEvents((Listener) instance, MicroClazz.instance);

        result.put(Keys.INSTANCE, instance);

        MicroClazz.loadedClassData.put(keyName, result);

        try {
            cls.getMethod("onScriptLoad").invoke(instance);
        } catch(Exception ignored){}
        return retWarn;
    }



    public static int compileJava(File javaFilePath) throws Exception {

        if (!javaFilePath.getName().endsWith(".java"))
            throw new Exception("Expect a file with '.java' extension.");

        if (!javaFilePath.exists())
            throw new Exception("File doesn't exist.");

        File outClassFile = new File(javaFilePath.getAbsolutePath().replaceFirst("\\.java$", ".class"));
        if (outClassFile.exists()) {
            if (outClassFile.lastModified() == javaFilePath.lastModified()) return 1;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
            throw new Exception("No Java compiler found! Are you running a JRE instead of a JDK?");

        String cp = buildClassPaths();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        // run() returns 0 on success
        int result = compiler.run(null, out, err,
                "-proc:none",           // skip annotation processing
                "-Xlint:-options",      // disable some warnings
                "-classpath", cp,
                javaFilePath.getAbsolutePath());

        if (result != 0) {
            String errors = err.toString(StandardCharsets.UTF_8);
            throw new Exception("Compilation failed:\n" + errors);
        }

        //set date
        File classFile = new File(javaFilePath.getAbsolutePath().replaceFirst("\\.java$", ".class"));
        classFile.setLastModified(javaFilePath.lastModified());
        return 0;
    }

    private static String buildClassPaths() throws Exception {
        ClassLoader cl = Bukkit.class.getClassLoader();

        if (!(cl instanceof java.net.URLClassLoader))
            throw new Exception("ClassLoader is not a URLClassLoader!");

        java.net.URL[] urls = ((java.net.URLClassLoader) cl).getURLs();

        StringBuilder paths = new StringBuilder();
        for (int i = 0; i < urls.length; i++) {
            paths.append(new File(urls[i].toURI()).getAbsolutePath());
            if (i < urls.length - 1) paths.append(File.pathSeparator);
        }

        return paths.toString();
    }
}
