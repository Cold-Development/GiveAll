package dev.padrewin.giveall;

import dev.padrewin.colddev.ColdPlugin;
import dev.padrewin.colddev.config.ColdSetting;
import dev.padrewin.colddev.manager.Manager;
import dev.padrewin.colddev.manager.PluginUpdateManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.padrewin.giveall.manager.CommandManager;
import dev.padrewin.giveall.manager.LocaleManager;
import dev.padrewin.giveall.setting.SettingKey;

import java.io.File;
import java.util.List;

public final class GiveAll extends ColdPlugin {

    private static GiveAll instance;

    public GiveAll() {
        super("Cold-Development", "GiveAll", 23387, null, LocaleManager.class, null);
        instance = this;
    }

    @Override
    public void enable() {
        instance = this;
        getManager(PluginUpdateManager.class);

        String name = getDescription().getName();
        getLogger().info("");
        getLogger().info("  ____ ___  _     ____  ");
        getLogger().info(" / ___/ _ \\| |   |  _ \\ ");
        getLogger().info("| |  | | | | |   | | | |");
        getLogger().info("| |__| |_| | |___| |_| |");
        getLogger().info(" \\____\\___/|_____|____/");
        getLogger().info("    " + name + " v" + getDescription().getVersion());
        getLogger().info("    Author(s): " + (String)getDescription().getAuthors().get(0));
        getLogger().info("    (c) Cold Development ❄");
        getLogger().info("");


        File configFile = new File(getDataFolder(), "en_US.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
    }

    @Override
    public void disable() {
        getLogger().info("Plugin unloaded.");
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                CommandManager.class
        );
    }

    @Override
    protected List<ColdSetting<?>> getColdConfigSettings() {
        return SettingKey.getKeys();
    }

    @Override
    protected String[] getColdConfigHeader() {
        return new String[] {
                "  ____  ___   _      ____   ",
                " / ___|/ _ \\ | |    |  _ \\  ",
                "| |   | | | || |    | | | | ",
                "| |___| |_| || |___ | |_| | ",
                " \\____|\\___/ |_____|_____/  ",
                "                           "
        };
    }

    public static GiveAll getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GiveAll instance is not initialized!");
        }
        return instance;
    }

    public String getItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        return item.getType().toString().replace("_", " ").toLowerCase();
    }

}