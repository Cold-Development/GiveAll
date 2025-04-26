package dev.padrewin.giveall.hook;

import dev.padrewin.giveall.GiveAll;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GiveAllPlaceholderExpansion extends PlaceholderExpansion {

    private final GiveAll plugin;
    private long lastExecutionTime;

    public GiveAllPlaceholderExpansion(GiveAll plugin) {
        this.plugin = plugin;
        this.lastExecutionTime = 0L;
    }

    public void setLastExecutionTime(long time) {
        this.lastExecutionTime = time;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "giveall";
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String placeholder) {
        if (player == null) {
            return null;
        }

        switch (placeholder.toLowerCase()) {
            case "last_execution":
                return String.valueOf(lastExecutionTime) + "ms";
            case "last_execution_color":
                if (lastExecutionTime < 100) {
                    return "&a";
                } else if (lastExecutionTime < 300) {
                    return "&6";
                } else {
                    return "&c";
                }
            case "last_execution_full":
                String color;
                if (lastExecutionTime < 100) {
                    color = "&a";
                } else if (lastExecutionTime < 300) {
                    color = "&6";
                } else {
                    color = "&c";
                }
                return color + "GiveAll executed in " + lastExecutionTime + "ms.";
            default:
                return null;
        }
    }
}
