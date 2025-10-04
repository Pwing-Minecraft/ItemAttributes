package net.pwing.itemattributes.util.menu;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.ItemUtils;
import net.pwing.itemattributes.message.Message;
import net.pwing.itemattributes.message.TextUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ItemBuilder {
    private Material material;
    private int count = 1;
    private Component name;
    private List<Component> lore;
    private final Set<ItemFlag> flags = new HashSet<>();
    private boolean hideTooltip;

    private ItemBuilder() {
    }

    public ItemBuilder material(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder count(int count) {
        this.count = count;
        return this;
    }

    public ItemBuilder name(Message message) {
        this.name = message.toComponent();
        return this;
    }

    public ItemBuilder name(Component name) {
        this.name = name;
        return this;
    }

    public ItemBuilder lore(List<Message> lore) {
        this.lore = lore.stream()
                .map(Message::toComponent)
                .toList();
        return this;
    }

    public ItemBuilder lore(Message... lore) {
        this.lore = Stream.of(lore)
                .map(Message::toComponent)
                .toList();
        return this;
    }

    public ItemBuilder hideItemDetails() {
        this.flags.addAll(List.of(ItemFlag.values()));
        return this;
    }

    public ItemBuilder hideTooltip() {
        this.hideTooltip = true;
        return this;
    }

    public ItemStack build() {
        if (this.material == null) {
            throw new IllegalStateException("Material cannot be null");
        }

        if (this.count <= 0) {
            throw new IllegalStateException("Count must be greater than 0");
        }

        ItemStack itemStack = new ItemStack(this.material, this.count);
        ItemMeta meta = itemStack.getItemMeta();
        if (this.name != null) {
            meta.setDisplayName(TextUtils.toLegacy(this.name));
        }

        if (this.lore != null) {
            meta.setLore(this.lore.stream()
                    .map(TextUtils::toLegacy)
                    .toList()
            );
        }

        if (!this.flags.isEmpty()) {
            meta.addItemFlags(this.flags.toArray(ItemFlag[]::new));
            if (this.flags.contains(ItemFlag.HIDE_ATTRIBUTES)) {
                ItemUtils.hideVisualAttributes(itemStack.getType(), meta);
            }
        }

        if (this.hideTooltip) {
            meta.setHideTooltip(true);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemBuilder builder() {
        return new ItemBuilder();
    }
}
