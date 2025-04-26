package dev.padrewin.giveall.commands;

import dev.padrewin.colddev.utils.StringPlaceholders;
import dev.padrewin.giveall.hook.GiveAllPlaceholderExpansion;
import dev.padrewin.giveall.setting.SettingKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import dev.padrewin.giveall.GiveAll;
import dev.padrewin.giveall.manager.CommandManager;
import dev.padrewin.giveall.manager.LocaleManager;

import java.util.*;

public class GiveCommand extends BaseCommand {

    public GiveCommand() {
        super("give", CommandManager.CommandAliases.GIVE);
    }

    @Override
    public void execute(@NotNull GiveAll plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);

        long startTime = System.currentTimeMillis(); // 🕒 Start timer

        if (args.length > 1) {
            localeManager.sendMessage(sender, "command-give-usage");
            return;
        }

        if (!sender.hasPermission("giveall.use")) {
            localeManager.sendMessage(sender, "no-permission");
            return;
        }

        if (!(sender instanceof Player)) {
            localeManager.sendMessage(sender, "only-players");
            return;
        }

        Player senderPlayer = (Player) sender;
        ItemStack itemInHand = senderPlayer.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.AIR) {
            localeManager.sendMessage(sender, "no-item-in-hand");
            return;
        }

        String itemName = plugin.getItemName(itemInHand);
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        String prefix = localeManager.getLocaleMessage("prefix");
        if (prefix == null) {
            plugin.getLogger().warning("Missing locale string: prefix");
            prefix = "";
        }

        boolean antiFraudEnabled = SettingKey.ANTI_FRAUD_SYSTEM.get();
        Map<String, String> givenIps = new HashMap<>();

        for (Player player : players) {
            if (player == senderPlayer) continue;

            if (antiFraudEnabled) {
                String ip = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : null;
                if (ip == null) continue;

                if (givenIps.containsKey(ip)) {
                    String winnerName = givenIps.get(ip);
                    localeManager.sendMessage(player, "already-received-item", StringPlaceholders.of("player", winnerName));
                    continue;
                }

                givenIps.put(ip, player.getName());
            }

            player.getInventory().addItem(itemInHand.clone());
            localeManager.sendMessage(player, "give-message", StringPlaceholders.builder("item", itemName)
                    .add("player", senderPlayer.getDisplayName())
                    .build());
        }

        localeManager.sendMessage(senderPlayer, "item-given", StringPlaceholders.of("item", itemName));

        // 🕒 Stop the timer and calculate execution duration
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 🧠 Update the last execution time for PlaceholderAPI placeholders
        GiveAllPlaceholderExpansion placeholderExpansion = plugin.getPlaceholderExpansion();
        if (placeholderExpansion != null) {
            placeholderExpansion.setLastExecutionTime(duration);
        }

        // 🖨️ Send execution time log to console with appropriate color
        String timerMessageKey;
        if (duration < 100) {
            timerMessageKey = "console-giveall-execute-timer-fast";
        } else if (duration < 300) {
            timerMessageKey = "console-giveall-execute-timer-medium";
        } else {
            timerMessageKey = "console-giveall-execute-timer-slow";
        }

        localeManager.sendMessage(Bukkit.getConsoleSender(), timerMessageKey, StringPlaceholders.of("time", duration));

        // 🔥 If execution took more than 500ms, send a special warning message
        if (duration > 500) {
            localeManager.sendMessage(Bukkit.getConsoleSender(), "console-giveall-long-warning", StringPlaceholders.of("time", duration));
        }
    }

    @Override
    public List<String> tabComplete(@NotNull GiveAll plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}
