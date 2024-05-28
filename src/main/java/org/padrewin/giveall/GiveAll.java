package org.padrewin.giveall;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Objects;

public final class GiveAll extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin loaded.");
        saveDefaultConfig();
        getCommand("giveall").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin unloaded.");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!commandSender.hasPermission("giveall.reload")) {
                    commandSender.sendMessage(applyColors(getTag() + getConfig().getString("messages.no_permission")));
                    return true;
                }
                reloadConfig();
                commandSender.sendMessage(applyColors(getTag() + getConfig().getString("messages.config_reloaded")));
                return true;
            }

            if (args[0].equalsIgnoreCase("version")) {
                if (!commandSender.hasPermission("giveall.version")) {
                    commandSender.sendMessage(applyColors(getTag() + getConfig().getString("messages.no_permission")));
                    return true;
                }
                String versionMessage = applyColors(getTag() + "&7Version: &a" + getDescription().getVersion());
                String authorMessage = applyColors(getTag() + "&7Developer: &c" + String.join(", ", getDescription().getAuthors()));
                String githubMessage = applyColors(getTag() + "&7GitHub: &bhttps://github.com/padrewin/giveall");

                commandSender.sendMessage(versionMessage);
                commandSender.sendMessage(authorMessage);
                commandSender.sendMessage(githubMessage);
                return true;
            }
        }

        if (!commandSender.hasPermission("giveall.use")) {
            commandSender.sendMessage(applyColors(getTag() + getConfig().getString("messages.no_permission")));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(applyColors(getTag() + getConfig().getString("messages.only_players")));
            return true;
        }

        Player senderPlayer = (Player) commandSender;
        String senderName = senderPlayer.getDisplayName();
        ItemStack itemInHand = senderPlayer.getInventory().getItemInMainHand();

        // Check if the player is holding an item
        if (itemInHand.getType() == Material.AIR) {
            senderPlayer.sendMessage(applyColors(getTag() + getConfig().getString("messages.no_item_in_hand")));
            return true;
        }

        // Get the item's display name or fallback to its material name
        String itemName = getItemName(itemInHand);

        // Log the item type for debugging purposes
        getLogger().info("Player " + senderName + " is holding: " + itemName);

        // Iterate through all online players and give them the item
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            if (player != senderPlayer) { // Exclude the executor from receiving the item
                player.getInventory().addItem(itemInHand.clone()); // Give a copy of the item to each player
                String giveMessage = Objects.requireNonNull(getConfig().getString("messages.give_message"))
                        .replace("{item}", itemName)
                        .replace("{player}", senderName);
                player.sendMessage(applyColors(getTag() + giveMessage));
            }
        }

        String itemGivenMessage = Objects.requireNonNull(getConfig().getString("messages.item_given"))
                .replace("{item}", itemName);
        senderPlayer.sendMessage(applyColors(getTag() + itemGivenMessage));

        return true;
    }

    private String getItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        return item.getType().toString().replace("_", " ").toLowerCase();
    }

    private String getTag() {
        return applyColors(getConfig().getString("messages.tag", "&8「&cGiveAll&8」&7»&f"));
    }


    private String applyColors(String message) {

        message = message.replaceAll("(?i)&(#[a-f0-9]{6})", "§$1");

        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }
}
