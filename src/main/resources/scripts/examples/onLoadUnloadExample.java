
import org.bukkit.Bukkit;
public final class onLoadUnloadExample {
    public void onScriptLoad() {
        Bukkit.getLogger().info("Script Loaded.");
    }
    public void onScriptUnLoad() {
        Bukkit.getLogger().info("Script UnLoaded.");
    }
}
