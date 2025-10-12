package net.pwing.itemattributes.requirement;

import me.redned.config.ConfigOption;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class RequiresComponentRequirement implements ItemRequirement<AttributableItem> {
    private static final Pattern VANILLA_COMPONENT_PATTERN = Pattern.compile("([\\w:]+)(?==)");

    @ConfigOption(name = "key", description = "The key of the component to require.", required = true)
    private NamespacedKey key;

    @ConfigOption(name = "value", description = "The value of the component to require.")
    private String value;

    @Override
    public boolean hasRequirement(AttributableItem context, @Nullable Player player) {
        ItemStack itemStack = context.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }

        boolean hasKey = meta.getPersistentDataContainer().has(this.key);
        if (this.value == null && hasKey) {
            return true;
        }

        // Not the cleanest way, but Spigot doesn't exactly have a great API here
        String vanillaComponents = meta.getAsComponentString();
        boolean hasComponent = VANILLA_COMPONENT_PATTERN.matcher(vanillaComponents).results().anyMatch(matcher ->
                matcher.group().equals(this.key.toString())
        );

        if (this.value == null && hasComponent) {
            return true;
        }

        if (this.value == null) {
            return false;
        }

        if (hasKey) {
            String pdcValue = meta.getPersistentDataContainer().get(this.key, PersistentDataType.STRING);
            return this.value.equals(pdcValue);
        }

        // Now extract the value from the vanilla components string
        Pattern valuePattern = Pattern.compile(this.key + "=(\\S+)");
        return valuePattern.matcher(vanillaComponents).results().anyMatch(matcher ->
                matcher.group(1).equals(this.value)
        );
    }

    @Override
    public ItemRequirementType<AttributableItem, ? extends ItemRequirement<AttributableItem>> getType() {
        return ItemRequirementType.REQUIRES_COMPONENT;
    }
}
