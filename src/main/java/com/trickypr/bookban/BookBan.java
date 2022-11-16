package com.trickypr.bookban;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.trickypr.bookban.events.CommandEvent;
import com.trickypr.bookban.events.PlayerEvent;

public class BookBan extends JavaPlugin {
    FileConfiguration config;
    ArrayList<ConfigDependant> configDependents;

    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        PlayerEvent playerEventHandler = new PlayerEvent(config);
        CommandEvent reloadCommand = new CommandEvent(this);

        Bukkit.getPluginManager().registerEvents(playerEventHandler, this);
        getCommand("bookban").setExecutor(reloadCommand);

        configDependents = new ArrayList<ConfigDependant>();
        configDependents.add(playerEventHandler);
    }

    public void onDisable() {

    }

    public void reloadPluginConfig() {
        reloadConfig();
        config = getConfig();

        // If configDependant has not been defined, we do not want to run the
        // rest of this function
        if (configDependents == null)
            return;

        for (ConfigDependant configDependent : configDependents) {
            configDependent.setConfig(config);
        }
    }
}
