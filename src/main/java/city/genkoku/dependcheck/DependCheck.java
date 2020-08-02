package city.genkoku.dependcheck;

import org.bukkit.plugin.java.JavaPlugin;

public final class DependCheck extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("dependCheck").setExecutor(new DependCheckCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
