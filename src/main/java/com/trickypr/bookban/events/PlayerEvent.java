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

    int maxInventorySize = 27448;
    int maxItemSize = 9269;
    int maxBookSize = 8000;

    boolean disabled = false;
    boolean debug = false;

    public PlayerEvent(FileConfiguration config) {
        setConfig(config);
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;

        maxInventorySize = config.getInt("limit.inventory");
        maxItemSize = config.getInt("limit.item");
        maxBookSize = config.getInt("limit.book");

        disabled = config.getBoolean("disabled");
        debug = config.getBoolean("debug");
    }

    private int getItemSize(ItemStack stack) {
        if (stack == null)
            return 0;

        int size = 0;

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

        size += stack.serializeAsBytes().length;

        return size;
    }

    // Return inventory size in bytes
    private int inventorySize(ItemStack[] contents) {
        int size = 0;
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

        int invSize = inventorySize(player.getInventory().getContents());
        int itemSize = getItemSize(item);

        if (debug) {
            Bukkit.getLogger().info("Player: " + player.getName() + ", Item: " + itemSize
                    + " bytes, Inventory: " + invSize + " bytes, Total: " + (invSize + itemSize) + " bytes.");
        }

        // Check if the item is larger than any single item is allowed to be
        if (itemSize > maxItemSize) {
            e.setCancelled(true);
            e.getItem().remove();
            Bukkit.getLogger().info("Player " + player.getName() + " tried to pick up an item that was too large.");
            player.sendMessage(ChatColor.RED + "You cannot pick up this item, it is to large (" + itemSize + " > "
                    + maxItemSize + ")");
            return;
        }

        // Check if the total inventory size is larger than the maximum
        // inventory size
        if (itemSize + invSize > maxInventorySize) {
            player.sendMessage(ChatColor.RED + "You cannot pick up this item, your inventory is to full ("
                    + (itemSize + invSize) + " > " + maxInventorySize + ")");
            Bukkit.getLogger().info("Player " + player.getName() + " tried to pick up an item that would make their inventory too large.");
            e.setCancelled(true);
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