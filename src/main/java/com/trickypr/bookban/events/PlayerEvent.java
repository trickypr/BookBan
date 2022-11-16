package com.trickypr.bookban.events;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import com.trickypr.bookban.ConfigDependant;

public class PlayerEvent implements Listener, ConfigDependant {
    FileConfiguration config;

    long maxInventorySize = 27448;
    long maxItemSize = 9269;
    long maxBookSize = 8000;

    boolean disabled = false;
    boolean debug = false;

    public PlayerEvent(FileConfiguration config) {
        setConfig(config);
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;

        maxInventorySize = config.getLong("limit.inventory");
        maxItemSize = config.getLong("limit.item");
        maxBookSize = config.getLong("limit.book");

        disabled = config.getBoolean("disabled");
        debug = config.getBoolean("debug");
    }

    private long getItemSize(ItemStack stack) {
        if (stack == null)
            return 0;

        long size = 0;

        BlockStateMeta meta = null;

        if (stack.hasItemMeta())
            if (stack.getItemMeta() instanceof BlockStateMeta)
                meta = (BlockStateMeta) stack.getItemMeta();

        // Item metadata is used for the contents of shulkerboxes, so we
        // need to find its contents and serialize it for the shulker box
        // size
        if (meta != null && meta.getBlockState() instanceof ShulkerBox) {
            ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
            size += inventorySize(shulker.getInventory().getContents());
        }

        // Counterintuitively, the serialize as bytes function takes significantly
        // longer to run because it tries to compress the file. Instead, we will
        // just use the string length, even though that is not a great proxy.
        size += stack.serialize().toString().getBytes(StandardCharsets.UTF_8).length;

        return size;
    }

    // Return inventory size in bytes
    private long inventorySize(ItemStack[] contents) {
        long size = 0;
        for (ItemStack stack : contents) {
            size += getItemSize(stack);
        }
        return size;
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (disabled)
            return;

        // We do not care about non-player entities for the moment
        if (!(e.getEntity() instanceof Player))
            return;

        LivingEntity entity = e.getEntity();
        Player player = (Player) entity;

        ItemStack item = e.getItem().getItemStack();

        long invSize = inventorySize(player.getInventory().getContents());
        long itemSize = getItemSize(item);

        if (debug) {
            Bukkit.getLogger().info("Player: " + player.getName() + ", Item: " + itemSize
                    + " bytes, Inventory: " + invSize + " bytes, Total: " + (invSize + itemSize) + " bytes.");
        }

        // Check if the item is larger than any single item is allowed to be
        if (itemSize > maxItemSize) {
            e.setCancelled(true);
            Bukkit.getLogger().info("Player " + player.getName() + " tried to pick up an item that was too large.");
            player.sendMessage(ChatColor.RED + "You cannot pick up this item, it is to large (" + itemSize + " > "
                    + maxItemSize + ")");
            return;
        }

        // Check if the total inventory size is larger than the maximum
        // inventory size
        if (itemSize + invSize > maxInventorySize) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot pick up this item, your inventory is to full ("
                    + (itemSize + invSize) + " > " + maxInventorySize + ")");
            Bukkit.getLogger().info("Player " + player.getName()
                    + " tried to pick up an item that would make their inventory too large.");
        }
    }

    @EventHandler
    public void onBookSave(PlayerEditBookEvent e) {
        if (disabled)
            return;

        // TODO: We should stop using depreciated apis
        List<String> pages = e.getNewBookMeta().getPages();
        String text = String.join("", pages);
        final byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        if (bytes.length > maxBookSize) {
            e.getPlayer().sendMessage(ChatColor.RED + "The contents of the book are too large (" + bytes.length + " > "
                    + maxBookSize + ")");
            e.setCancelled(true);
        }
    }
}