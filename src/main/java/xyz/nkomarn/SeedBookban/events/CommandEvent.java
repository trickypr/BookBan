package xyz.nkomarn.SeedBookban.events;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import xyz.nkomarn.SeedBookban.SeedBookban;

public class CommandEvent implements CommandExecutor {
    SeedBookban plugin;

    public CommandEvent(SeedBookban plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadPluginConfig();

        return true;
    }

}
