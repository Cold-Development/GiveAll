package dev.padrewin.giveall.commands;

import dev.padrewin.giveall.GiveAll;
import dev.padrewin.giveall.manager.CommandManager;
import dev.padrewin.giveall.manager.LocaleManager;
import dev.padrewin.giveall.setting.SettingKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import java.util.Collections;
import java.util.List;

public class InfoCommand extends BaseCommand {

    public InfoCommand() {
        super("info", CommandManager.CommandAliases.INFO);
    }

    @Override
    public void execute(GiveAll plugin, CommandSender sender, String[] args) {
        LocaleManager localeManager = plugin.getManager(LocaleManager.class);

        boolean placeholderApiHooked = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        boolean antiFraudEnabled = SettingKey.ANTI_FRAUD_SYSTEM.get();

        String baseColor = localeManager.getLocaleMessage("base-command-color");

        localeManager.sendCustomMessage(sender, baseColor + "");
        localeManager.sendCustomMessage(sender, baseColor + "<g:#635AA7:#E6D4F8:#9E48F6>GiveAll" + baseColor + " Information");
        localeManager.sendCustomMessage(sender, baseColor + "PlaceholderAPI Hook: " + (placeholderApiHooked ? "&a✔" : "&c✘"));
        localeManager.sendCustomMessage(sender, baseColor + "Anti-Fraud System: " + (antiFraudEnabled ? "&a✔" : "&c✘"));
        localeManager.sendCustomMessage(sender, baseColor + "");
    }

    @Override
    public List<String> tabComplete(GiveAll plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}