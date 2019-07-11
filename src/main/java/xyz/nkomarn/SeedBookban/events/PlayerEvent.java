package xyz.nkomarn.SeedBookban.events;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

@SuppressWarnings("deprecation")
public class PlayerEvent implements Listener {

    // Return inventory size in bytes
    private int inventorySize(ItemStack[] contents) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(os);
            data.writeObject(contents);
            data.close();
            os.close();
            return os.toByteArray().length;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return 5000;
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(os);
            data.writeObject(item);
            data.close();

            int invSize = inventorySize(e.getPlayer().getInventory().getContents());
            int itemSize = os.toByteArray().length;
            os.close();

            // Check to make sure inventory contents aren't over 1 MB
            e.getPlayer().sendMessage(itemSize + " bytes. Inventory: " + invSize + " bytes.");
            if (itemSize + invSize > 1000000) {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8&l(&c&l!&8&l) &cPicking up this item would overflow your maximum inventory size, thus it will not be picked up."));
                e.setCancelled(true);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onBookSave(PlayerEditBookEvent e) {
        List<String> pages = e.getNewBookMeta().getPages();
        String text = String.join("", pages);
        final byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 8000) {
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8&l(&c&l!&8&l) &cThe contents of the book are over 8KB, so this book will not be saved to prevent potential exploits."));
            e.setCancelled(true);
        }
    }

    /*@EventHandler
    public void onClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Inventory destination = e.getWhoClicked().getInventory();
        Player player = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType() != Material.AIR) try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(os);
            data.writeObject(item);
            data.close();
            int itemSize = os.toByteArray().length;
            os.close();

            // Check to make sure shulker box size isn't over 15KB
            if (itemSize > 15000) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8&l(&c&l!&8&l) &cThis shulker box is 15KB and overflows the maximum allowed size."));
                e.setCancelled(true);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }*/
}