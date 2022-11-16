package com.trickypr.bookban.events;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.trickypr.bookban.BookBan;

public class CommandEvent implements CommandExecutor {
    BookBan plugin;

    public CommandEvent(BookBan plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("BookBan v" + plugin.getDescription().getVersion());
            sender.sendMessage("Usage: /bookban reload");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bookban.reload")) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }

            plugin.reloadPluginConfig();

            sender.sendMessage("BookBan config reloaded!");
            Bukkit.getLogger().info("BookBan config reloaded!");

            return true;
        }

        return true;
    }

}
