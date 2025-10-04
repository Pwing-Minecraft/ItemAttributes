package net.pwing.itemattributes.requirement;

import me.redned.config.ConfigOption;
import me.redned.config.PostProcessable;
import net.pwing.itemattributes.feature.items.Items;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RequiresItemRequirement implements ItemRequirement<AttributableItem>, PostProcessable {
    @ConfigOption(name = "types", description = "The types of items that are required for this attribute.", required = true)
    private List<String> typesKeys;

    private final List<ItemStack> items = new ArrayList<>();

    @Override
    public void postProcess() {
        for (String type : this.typesKeys) {
            // Item tag
            if (type.startsWith("#")) {
                Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_ITEMS, NamespacedKey.fromString(type.substring(1)), Material.class);
                this.items.addAll(tag.getValues().stream().map(ItemStack::new).toList());
            } else {
                this.items.add(Items.createItem(NamespacedKey.fromString(type)));
            }
        }
    }

    @Override
    public boolean hasRequirement(AttributableItem context, @Nullable Player player) {
        for (ItemStack item : this.items) {
            if (Items.isSameItemType(item, context.getItemStack())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemRequirementType<AttributableItem, ? extends ItemRequirement<AttributableItem>> getType() {
        return ItemRequirementType.REQUIRES_ITEM;
    }
}
