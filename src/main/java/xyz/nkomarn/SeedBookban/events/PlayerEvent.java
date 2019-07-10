package xyz.nkomarn.SeedBookban.events;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

@SuppressWarnings("deprecation")
public class PlayerEvent implements Listener {
    
    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(os);
            data.writeObject(item);
            data.close();

            String base64 = Base64Coder.encodeLines(os.toByteArray());
            int itemSize = base64.getBytes("UTF-8").length;
            /*if (itemSize > 4096) {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8&l(&c&l!&8&l) &cThe item you are trying to pick up is over 4KB."));
                e.setCancelled(true);
            }*/

            // Check to make sure inv contents aren't over 1.5 MB
            System.out.println(inventorySize(e.getPlayer().getInventory().getContents()));
            if (itemSize + inventorySize(e.getPlayer().getInventory().getContents()) > 1500000) {
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
        try {
            final byte[] bytes = text.getBytes("UTF-8");
            int size = bytes.length;
            System.out.println(bytes.length);
            if (size > 4096) {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8&l(&c&l!&8&l) &cThe contents of the book are over 4KB, so this book will not be saved to prevent exploits."));
                e.setCancelled(true);
            }
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    private int inventorySize(ItemStack[] contents) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(os);
            data.writeObject(contents);
            data.close();

            String base64 = Base64Coder.encodeLines(os.toByteArray());
            int itemSize = base64.getBytes("UTF-8").length;
            return itemSize;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return 5000;
        }
    }
}