package net.pwing.itemattributes.feature.placeholder.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.ItemAttribute;
import net.pwing.itemattributes.attribute.SlotGroup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ItemAttributesExpansion extends PlaceholderExpansion {
    private final ItemAttributes plugin;

    public ItemAttributesExpansion(ItemAttributes plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return this.plugin.getDescription().getName().toLowerCase(Locale.ROOT);
    }

    @NotNull
    @Override
    public String getAuthor() {
        return this.plugin.getDescription().getAuthors().getFirst();
    }

    @NotNull
    @Override
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Nullable
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] split = params.split("_");
        // No data for us to parse
        if (split.length < 2) {
            return null;
        }

        // Format: %itemattributes_attribute_<attr>_<slot>%
        // Example: %itemattributes_attribute_health_head%
        if (player == null) {
            return null;
        }

        if (params.startsWith("attribute_") && split.length == 3) {
            String attributeKey = split[1];
            String slotKey = split[2];

            ItemAttribute attribute = this.plugin.getAttributesConfig().getAttributes().get(attributeKey);
            if (attribute == null) {
                return null;
            }

            SlotGroup group;

            try {
                group = SlotGroup.valueOf(slotKey.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                return null;
            }

            double rawValue = 0;
            for (EquipmentSlot slot : group.getSlots()) {
                ItemStack item = player.getInventory().getItem(slot);
                if (item == null || item.getType().isAir()) {
                    continue;
                }

                rawValue += this.plugin.getAttributeManager().getAttributeValue(item.getItemMeta(), attribute).doubleValue();
            }

            return attribute.getType().convert(rawValue).toString();
        }

        return null;
    }
}
