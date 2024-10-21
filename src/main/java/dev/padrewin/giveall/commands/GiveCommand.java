package dev.padrewin.giveall.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import dev.padrewin.giveall.GiveAll;
import dev.padrewin.giveall.manager.CommandManager;
import dev.padrewin.giveall.manager.LocaleManager;

import java.util.Collection;
import java.util.List;

public class GiveCommand extends BaseCommand {

    public GiveCommand() {
        super("give", CommandManager.CommandAliases.GIVE);
    }

    @Override
    public void execute(@NotNull GiveAll plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);

        if (args.length == 0) {
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

            for (Player player : players) {
                if (player != senderPlayer) {
                    player.getInventory().addItem(itemInHand.clone());
                    String giveMessage = localeManager.getLocaleMessage("give-message");
                    if (giveMessage == null) {
                        plugin.getLogger().warning("Missing locale string: give-message");
                        continue;
                    }

                    String finalGiveMessage = prefix + giveMessage.replace("{item}", itemName).replace("{player}", senderPlayer.getDisplayName());
                    player.sendMessage(finalGiveMessage);
                }
            }

            String itemGivenMessage = localeManager.getLocaleMessage("item-given");
            if (itemGivenMessage == null) {
                plugin.getLogger().warning("Missing locale string: item-given");
                return;
            }
            itemGivenMessage = itemGivenMessage.replace("{item}", itemName);

            String finalMessage = prefix + itemGivenMessage;

            senderPlayer.sendMessage(finalMessage);
            return;
        }

        localeManager.sendMessage(sender, "invalid-command");
    }

    @Override
    public List<String> tabComplete(@NotNull GiveAll plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}
