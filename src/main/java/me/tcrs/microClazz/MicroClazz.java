package me.tcrs.microClazz;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MicroClazz extends JavaPlugin {

    public static File mainDataFolder;
    public static HashMap<String, Map<Byte, Object>> loadedClassData = new HashMap<>();
    public static MicroClazz instance;

    @Override
    public void onEnable() {

        instance = this;
        mainDataFolder = getDataFolder();

        // Ensure plugin folder exists
        if (!mainDataFolder.exists()) {
            mainDataFolder.mkdirs();
            Bukkit.getLogger().info("Created plugin folder: " + mainDataFolder.getAbsolutePath());
        }

        // Save config.yml
        saveDefaultConfig();

        // save custom resource from JAR
        saveResource("scripts/examples/eventExample.java");
        saveResource("scripts/examples/onLoadUnloadExample.java");

        // Register commands
        this.getCommand("microclazz").setExecutor(new commandExecutor());
        this.getCommand("microclazz").setTabCompleter(new tabCompleter());

        // Run commands on enable
        List<String> commands = getConfig().getStringList("run_on_enable");
        if (commands != null) {
            for (String cmd : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }

        Bukkit.getLogger().info("MicroClazz enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Saves a file from the plugin JAR to the plugin folder.
     * Works like saveDefaultConfig(), but for any resource.
     * Won't overwrite existing files.
     */
    public void saveResource(String resourcePath) {
        File outFile = new File(getDataFolder(), resourcePath);

        if (outFile.exists()) return;

        outFile.getParentFile().mkdirs();

        try (InputStream in = getResource(resourcePath)) {
            if (in == null) {
                getLogger().warning("Resource not found in JAR: " + resourcePath);
                return;
            }
            Files.copy(in, outFile.toPath());
        } catch (IOException e) {
            getLogger().severe("Failed to save resource " + resourcePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
