package xyz.acrylicstyle.hideAndShow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HideAndShowPlugin extends JavaPlugin implements Listener {
    private final Set<UUID> vanished = new HashSet<>();

    // change these messages if you want to change message / text
    private static final ItemStack HIDE_ITEM = createItemStack(Material.GREEN_DYE, "" + ChatColor.GREEN + "プレイヤーを非表示にする"); // Hide Players
    private static final ItemStack SHOW_ITEM = createItemStack(Material.GRAY_DYE, "" + ChatColor.GREEN + "プレイヤーを表示する"); // Show Players
    private static final String MESSAGE_SHOWN_PLAYER = ChatColor.GREEN + "プレイヤーを「表示」にしました。"; // Players are now shown.
    private static final String MESSAGE_HIDDEN_PLAYER = ChatColor.GREEN + "プレイヤーを「" + ChatColor.RED + "非表示" + ChatColor.GREEN + "」にしました。"; // Players are now hidden.

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (vanished.contains(e.getPlayer().getUniqueId())) {
            hide(e.getPlayer());
        } else {
            show(e.getPlayer());
        }
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.getUniqueId().equals(e.getPlayer().getUniqueId())) return;
            if (vanished.contains(p.getUniqueId())) p.hidePlayer(this, e.getPlayer());
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (HIDE_ITEM.isSimilar(e.getItem())) {
                hide(e.getPlayer());
            } else if (SHOW_ITEM.isSimilar(e.getItem())) {
                show(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (HIDE_ITEM.isSimilar(e.getCurrentItem()) || SHOW_ITEM.isSimilar(e.getCurrentItem())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (HIDE_ITEM.isSimilar(e.getItemDrop().getItemStack()) || SHOW_ITEM.isSimilar(e.getItemDrop().getItemStack())) e.setCancelled(true);
    }

    public void show(Player player) {
        vanished.remove(player.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(p -> player.showPlayer(this, p));
        player.getInventory().setItem(8, HIDE_ITEM);
        player.sendMessage(MESSAGE_SHOWN_PLAYER);
    }

    public void hide(Player player) {
        vanished.add(player.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(p -> player.hidePlayer(this, p));
        player.getInventory().setItem(8, SHOW_ITEM);
        player.sendMessage(MESSAGE_HIDDEN_PLAYER);
    }

    public static ItemStack createItemStack(Material material, String displayName) {
        return createItemStack(material, displayName, false, (String[]) null);
    }

    public static ItemStack createItemStack(Material material, String displayName, boolean enchanted, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if (displayName != null) meta.setDisplayName(displayName);
        if (lore != null) meta.setLore(Arrays.asList(lore));
        if (enchanted) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }
}
