package dev.padrewin.giveall.manager;

import dev.padrewin.colddev.ColdPlugin;
import dev.padrewin.colddev.manager.AbstractLocaleManager;
import dev.padrewin.colddev.utils.StringPlaceholders;
import dev.padrewin.giveall.hook.NexoHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.minecart.CommandMinecart;

public class LocaleManager extends AbstractLocaleManager {

    private static final String ITEM_PLACEHOLDER = "%item%";

    public LocaleManager(ColdPlugin coldPlugin) {
        super(coldPlugin); // Calls super constructor first
    }

    @Override
    public void reload() {
        NexoHook.reset();
        super.reload();
    }

    /**
     * Single funnel for every message sent to a player or the console, locale file or custom.
     * Glyph tags are still in the text at this point and are turned into Nexo glyph components
     * here, so the glyph keeps its font.
     */
    @Override
    protected void handleMessage(CommandSender sender, String message) {
        // Command blocks and minecarts must be messaged from their own region thread on Folia
        if (sender instanceof BlockCommandSender && !Bukkit.isPrimaryThread()) {
            Location location = ((BlockCommandSender) sender).getBlock().getLocation();
            this.coldPlugin.getScheduler().runTaskAtLocation(location, () -> this.deliver(sender, message));
        } else if (sender instanceof CommandMinecart && !Bukkit.isPrimaryThread()) {
            this.coldPlugin.getScheduler().runTaskAtEntity((CommandMinecart) sender, () -> this.deliver(sender, message));
        } else {
            this.deliver(sender, message);
        }
    }

    /**
     * Sends a locale message in which {@code %item%} is replaced by the item's own name component,
     * so its colours, fonts and glyphs reach the player untouched (a plain string can't carry them).
     */
    public void sendItemMessage(CommandSender sender, String key, StringPlaceholders placeholders, Component item) {
        String message = this.getLocaleMessage(key, placeholders);
        if (message == null || message.isEmpty())
            return;

        String colored = this.getLocaleMessage("prefix") + message;
        TextComponent.Builder builder = Component.text();
        int last = 0;
        int index;
        while ((index = colored.indexOf(ITEM_PLACEHOLDER, last)) != -1) {
            builder.append(this.segment(colored, last, index));
            builder.append(item.applyFallbackStyle(this.styleAt(colored, index)));
            last = index + ITEM_PLACEHOLDER.length();
        }
        builder.append(this.segment(colored, last, colored.length()));

        Component component = builder.build();
        if (sender instanceof BlockCommandSender && !Bukkit.isPrimaryThread()) {
            Location location = ((BlockCommandSender) sender).getBlock().getLocation();
            this.coldPlugin.getScheduler().runTaskAtLocation(location, () -> sender.sendMessage(component));
        } else if (sender instanceof CommandMinecart && !Bukkit.isPrimaryThread()) {
            this.coldPlugin.getScheduler().runTaskAtEntity((CommandMinecart) sender, () -> sender.sendMessage(component));
        } else {
            sender.sendMessage(component);
        }
    }

    /**
     * A slice of the message, starting with the colours that were active where it begins.
     */
    private Component segment(String colored, int start, int end) {
        String text = ChatColor.getLastColors(colored.substring(0, start)) + colored.substring(start, end);
        return NexoHook.isEnabled() && NexoHook.hasGlyphs(text)
                ? NexoHook.toComponent(text)
                : LegacyComponentSerializer.legacySection().deserialize(text);
    }

    /**
     * The style (colours and formats) active at the given position, so the item name blends into the message.
     */
    private Style styleAt(String colored, int position) {
        String colors = ChatColor.getLastColors(colored.substring(0, position));
        return LegacyComponentSerializer.legacySection().deserialize(colors + "x").compact().style();
    }

    private void deliver(CommandSender sender, String message) {
        if (NexoHook.isEnabled() && NexoHook.hasGlyphs(message)) {
            NexoHook.sendMessage(sender, message);
        } else {
            super.handleMessage(sender, message);
        }
    }

}
