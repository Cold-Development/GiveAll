package dev.padrewin.giveall.hook;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Soft hook into Nexo, used to render {@code <glyph:id>} tags found in locale messages
 * as Nexo glyph components, and to resolve the display name of Nexo custom items.
 * <p>
 * Every reference to a Nexo class lives in this file, so the class is only ever loaded
 * when Nexo is actually present on the server.
 */
public final class NexoHook {

    private static final Pattern GLYPH_PATTERN = Pattern.compile("<glyph:([a-zA-Z0-9_.\\-]+)>");

    private static Boolean enabled;

    private NexoHook() {
    }

    /**
     * @return true if Nexo is installed and enabled on this server
     */
    public static boolean isEnabled() {
        if (enabled == null)
            enabled = Bukkit.getPluginManager().isPluginEnabled("Nexo");
        return enabled;
    }

    /**
     * Called on plugin reload, in case Nexo was installed/removed in the meantime.
     */
    public static void reset() {
        enabled = null;
    }

    /**
     * @return true if the text contains at least one {@code <glyph:id>} tag
     */
    public static boolean hasGlyphs(String text) {
        return text != null && text.indexOf('<') != -1 && GLYPH_PATTERN.matcher(text).find();
    }

    /**
     * Turns already-coloured (§) text into a component in which every {@code <glyph:id>} tag is
     * Nexo's own glyph component. The component keeps the glyph's font, which is what makes the
     * client draw the texture instead of a plain symbol; a plain string can't carry a font.
     * Unknown glyphs keep their tag in the surrounding text.
     */
    public static Component toComponent(String colored) {
        Matcher matcher = GLYPH_PATTERN.matcher(colored);
        TextComponent.Builder builder = Component.text();
        int last = 0;
        while (matcher.find()) {
            Component glyph = glyphComponent(matcher.group(1));
            if (glyph == null)
                continue;
            builder.append(legacySegment(colored, last, matcher.start()));
            builder.append(glyph);
            last = matcher.end();
        }
        builder.append(legacySegment(colored, last, colored.length()));
        return builder.build();
    }

    public static void sendMessage(CommandSender sender, String colored) {
        sender.sendMessage(toComponent(colored));
    }

    /**
     * A slice of the text, starting with the colours that were active where the slice begins,
     * since every slice is deserialized on its own.
     */
    private static Component legacySegment(String colored, int start, int end) {
        String segment = ChatColor.getLastColors(colored.substring(0, start)) + colored.substring(start, end);
        return LegacyComponentSerializer.legacySection().deserialize(segment);
    }

    /**
     * Resolves the name of a Nexo custom item as a component, for items that carry their name in the
     * {@code item_name} component rather than as a display name. The component keeps its fonts and colours.
     *
     * @return the item name, or null if this is not a Nexo item
     */
    public static Component itemName(ItemStack item) {
        if (item == null || !isEnabled())
            return null;

        try {
            String id = NexoItems.idFromItem(item);
            if (id == null)
                return null;

            ItemBuilder builder = NexoItems.itemFromId(id);
            if (builder == null)
                return Component.text(id);

            Component name = builder.getItemName();
            if (name == null)
                name = builder.getCustomName();

            return name != null ? name : Component.text(id);
        } catch (Throwable ignored) {
            // Nexo present but not ready yet (items load async), or an incompatible Nexo version
            return null;
        }
    }

    private static Component glyphComponent(String id) {
        try {
            Glyph glyph = NexoPlugin.instance().fontManager().glyphFromID(id);
            return glyph != null ? glyph.glyphComponent() : null;
        } catch (Throwable ignored) {
            // Nexo present but not ready yet (glyphs load async), or an incompatible Nexo version
            return null;
        }
    }

}
