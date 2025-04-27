package dev.padrewin.giveall;

import dev.padrewin.colddev.ColdPlugin;
import dev.padrewin.colddev.config.ColdSetting;
import dev.padrewin.colddev.manager.Manager;
import dev.padrewin.colddev.manager.PluginUpdateManager;
import dev.padrewin.giveall.hook.GiveAllPlaceholderExpansion;
import dev.padrewin.giveall.util.GiveAllLogger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.padrewin.giveall.manager.CommandManager;
import dev.padrewin.giveall.manager.LocaleManager;
import dev.padrewin.giveall.setting.SettingKey;

import java.io.File;
import java.util.List;

import static dev.padrewin.colddev.manager.AbstractDataManager.ANSI_BOLD;
import static dev.padrewin.colddev.manager.AbstractDataManager.ANSI_LIGHT_BLUE;

public final class GiveAll extends ColdPlugin {

    /**
     * Console colors
     */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CHINESE_PURPLE = "\u001B[38;5;93m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_AQUA = "\u001B[36m";
    private static final String ANSI_PINK = "\u001B[35m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private static GiveAll instance;
    private GiveAllPlaceholderExpansion placeholderExpansion;
    private GiveAllLogger loggerManager;

    public GiveAll() {
        super("Cold-Development", "GiveAll", 23387, null, LocaleManager.class, null);
        instance = this;
    }

    @Override
    public void enable() {
        instance = this;
        this.loggerManager = new GiveAllLogger(this);
        getManager(PluginUpdateManager.class);

        // Initialize PlaceholderAPI Hook
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderExpansion = new GiveAllPlaceholderExpansion(this);
            this.placeholderExpansion.register();
            getLogger().info(ANSI_LIGHT_BLUE + "PlaceholderAPI hook registered successfully. " + ANSI_BOLD + ANSI_GREEN + "✔" + ANSI_RESET);
        } else {
            this.placeholderExpansion = null;
            getLogger().warning(ANSI_LIGHT_BLUE + "PlaceholderAPI not found. " + ANSI_BOLD + ANSI_RED + "✘" + ANSI_RESET);
        }

        String name = getDescription().getName();
        getLogger().info("");
        getLogger().info(ANSI_CHINESE_PURPLE + "  ____ ___  _     ____  " + ANSI_RESET);
        getLogger().info(ANSI_PINK + " / ___/ _ \\| |   |  _ \\ " + ANSI_RESET);
        getLogger().info(ANSI_CHINESE_PURPLE + "| |  | | | | |   | | | |" + ANSI_RESET);
        getLogger().info(ANSI_PINK + "| |__| |_| | |___| |_| |" + ANSI_RESET);
        getLogger().info(ANSI_CHINESE_PURPLE + " \\____\\___/|_____|____/ " + ANSI_RESET);
        getLogger().info("    " + ANSI_GREEN + name + ANSI_RED + " v" + getDescription().getVersion() + ANSI_RESET);
        getLogger().info(ANSI_PURPLE + "    Author(s): " + ANSI_PURPLE + getDescription().getAuthors().get(0) + ANSI_RESET);
        getLogger().info(ANSI_AQUA + "    (c) Cold Development ❄" + ANSI_RESET);
        getLogger().info("");

        File configFile = new File(getDataFolder(), "en_US.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
    }

    @Override
    public void disable() {
        if (loggerManager != null && SettingKey.SAVE_GIVEALL_LOGS.get()) {
            loggerManager.archiveLogs();
        }

        getLogger().info("");
        getLogger().info(ANSI_CHINESE_PURPLE + "GiveAll disabled." + ANSI_RESET);
        getLogger().info("");
    }


    public GiveAllLogger getLoggerManager() {
        return loggerManager;
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
                " ██████╗ ██████╗ ██╗     ██████╗ ",
                "██╔════╝██╔═══██╗██║     ██╔══██╗",
                "██║     ██║   ██║██║     ██║  ██║",
                "██║     ██║   ██║██║     ██║  ██║",
                "╚██████╗╚██████╔╝███████╗██████╔╝",
                " ╚═════╝ ╚═════╝ ╚══════╝╚═════╝ ",
                "                                 "
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

    public GiveAllPlaceholderExpansion getPlaceholderExpansion() {
        return placeholderExpansion;
    }


}