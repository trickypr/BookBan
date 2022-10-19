package com.trickypr.bookban.events;

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
        plugin.reloadPluginConfig();

        return true;
    }

}
