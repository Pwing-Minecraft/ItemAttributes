package net.pwing.itemattributes.requirement;

import me.redned.config.ConfigOption;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class RequiresComponentRequirement implements ItemRequirement<AttributableItem> {
    private static final Pattern VANILLA_COMPONENT_PATTERN = Pattern.compile("([\\w:]+)(?==)");

    @ConfigOption(name = "key", description = "The key of the component to require.", required = true)
    private NamespacedKey key;

    @Override
    public boolean hasRequirement(AttributableItem context, @Nullable Player player) {
        ItemStack itemStack = context.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }

        // First check the PDC
        if (meta.getPersistentDataContainer().has(this.key)) {
            return true;
        }

        // Not the cleanest way but Spigot doesn't exactly have a great API here
        String vanillaComponents = meta.getAsComponentString();
        return VANILLA_COMPONENT_PATTERN.matcher(vanillaComponents).results().anyMatch(matcher -> {
            return matcher.group().equals(this.key.toString());
        });
    }

    @Override
    public ItemRequirementType<AttributableItem, ? extends ItemRequirement<AttributableItem>> getType() {
        return ItemRequirementType.REQUIRES_COMPONENT;
    }
}
