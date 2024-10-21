package dev.padrewin.giveall.commands;

import dev.padrewin.colddev.utils.StringPlaceholders;
import dev.padrewin.giveall.GiveAll;
import dev.padrewin.giveall.manager.CommandManager;
import dev.padrewin.giveall.manager.LocaleManager;
import dev.padrewin.giveall.setting.SettingKey;
import org.bukkit.command.CommandSender;

/**
 * Handles the commands for the root command.
 *
 * @author padrewin
 */
public class Commander extends CommandHandler {

    public Commander(GiveAll plugin) {
        super(plugin, "giveall", CommandManager.CommandAliases.ROOT);

        // Register commands.
        this.registerCommand(new HelpCommand(this));
        this.registerCommand(new GiveCommand());
        this.registerCommand(new ReloadCommand());
        this.registerCommand(new VersionCommand());
    }

    @Override
    public void noArgs(CommandSender sender) {
        try {
            String redirect = SettingKey.BASE_COMMAND_REDIRECT.get().trim().toLowerCase();
            BaseCommand command = this.registeredCommands.get(redirect);
            CommandHandler handler = this.registeredHandlers.get(redirect);
            if (command != null) {
                if (!command.hasPermission(sender)) {
                    this.plugin.getManager(LocaleManager.class).sendMessage(sender, "no-permission");
                    return;
                }
                command.execute(this.plugin, sender, new String[0]);
            } else if (handler != null) {
                if (!handler.hasPermission(sender)) {
                    this.plugin.getManager(LocaleManager.class).sendMessage(sender, "no-permission");
                    return;
                }
                handler.noArgs(sender);
            } else {
                VersionCommand.sendInfo(this.plugin, sender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unknownCommand(CommandSender sender, String[] args) {
        this.plugin.getManager(LocaleManager.class).sendMessage(sender, "unknown-command", StringPlaceholders.of("input", args[0]));
    }
}
