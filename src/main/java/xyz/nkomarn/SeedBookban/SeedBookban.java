package xyz.nkomarn.SeedBookban;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.nkomarn.SeedBookban.events.PlayerEvent;

public class SeedBookban extends JavaPlugin {
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerEvent(), this);
    }

    public void onDisable() {
        
    }
}
