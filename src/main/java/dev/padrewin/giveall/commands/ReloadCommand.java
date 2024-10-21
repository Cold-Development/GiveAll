package dev.padrewin.giveall.commands;

import java.util.Collections;
import java.util.List;
import dev.padrewin.giveall.GiveAll;
import dev.padrewin.giveall.manager.CommandManager;
import dev.padrewin.giveall.manager.LocaleManager;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand() {
        super("reload", CommandManager.CommandAliases.RELOAD);
    }

    @Override
    public void execute(GiveAll plugin, CommandSender sender, String[] args) {
        if (!sender.hasPermission("giveall.reload")) {
            plugin.getManager(LocaleManager.class).sendMessage(sender, "no-permission");
            return;
        }

        plugin.reloadConfig();
        plugin.getManager(LocaleManager.class).reload();
        plugin.getManager(LocaleManager.class).sendMessage(sender, "config-reloaded");
    }

    @Override
    public List<String> tabComplete(GiveAll plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
