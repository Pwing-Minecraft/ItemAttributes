package net.pwing.itemattributes.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.ItemAttribute;
import net.pwing.itemattributes.item.event.ItemRenderEvent;
import net.pwing.itemattributes.item.event.PlayerItemRenderEvent;
import net.pwing.itemattributes.item.lore.LoreCreator;
import net.pwing.itemattributes.item.renderer.ItemRenderer;
import net.pwing.itemattributes.item.renderer.replacer.ComponentReplacer;
import net.pwing.itemattributes.item.renderer.replacer.TextReplacer;
import net.pwing.itemattributes.item.slot.SlotHolder;
import net.pwing.itemattributes.item.tier.ItemTier;
import net.pwing.itemattributes.message.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributableItem {
    private static final Component NO_ITALIC = Component.empty().decoration(TextDecoration.ITALIC, false);

    private final ItemStack itemStack;
    private final ItemTemplate template;

    private final Map<ItemAttribute, Number> attributes;
    private final List<SlotHolder> slots;
    private final ItemTier tier;

    private String name;
    private final String description;

    public AttributableItem(ItemManager manager, ItemStack itemStack, ItemTemplate template) {
        this.itemStack = itemStack;
        this.template = template;

        this.attributes = readAttributes(itemStack);
        this.slots = readSlots(manager, itemStack);
        this.tier = manager.getTier(itemStack);
        this.name = manager.getName(itemStack);
        this.description = manager.getDescription(itemStack);
    }

    public Map<ItemAttribute, Number> getAttributes() {
        return Map.copyOf(this.attributes);
    }

    public List<SlotHolder> getSlots() {
        return List.copyOf(this.slots);
    }

    @Nullable
    public ItemTier getTier() {
        return this.tier;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;

        ItemAttributes.getInstance().getItemManager().setName(this.itemStack, name);
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    public Component renderFromTemplate(String template) {
        for (ItemRenderer renderer : ItemRenderer.RENDERERS) {
            template = renderer.render(template, this, new TextReplacer(template));
        }

        return MiniMessage.miniMessage().deserialize(template);
    }

    public Component render(Component component) {
        for (ItemRenderer renderer : ItemRenderer.RENDERERS) {
            component = renderer.render(component, this, new ComponentReplacer(component));
        }

        return component;
    }

    public ItemTemplate getTemplate() {
        return this.template;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void render(@Nullable Player viewer) {
        ItemMeta meta = this.itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        if (this.template.getFlags() != null) {
            meta.addItemFlags(this.template.getFlags().toArray(ItemFlag[]::new));
        }

        meta.setHideTooltip(false);

        Component displayName = NO_ITALIC.append(this.template.getName().render(this));
        meta.setDisplayName(ChatColor.RESET + TextUtils.toLegacy(displayName));

        List<Component> lore = new ArrayList<>();
        for (LoreCreator creator : this.template.getLore()) {
            if (!creator.shouldCreateLines(this, viewer)) {
                continue;
            }

            lore.addAll(creator.createLines(this, viewer));
        }

        ItemRenderEvent event;
        if (viewer != null) {
            event = new PlayerItemRenderEvent(viewer, this, lore);
        } else {
            event = new ItemRenderEvent(this, lore);
        }

        meta.setLore(event.getRenderedLines().stream().map(TextUtils::toLegacy).toList());
        this.itemStack.setItemMeta(meta);
    }

    public void reapplyAttributes() {
        this.attributes.clear();
        this.attributes.putAll(readAttributes(this.itemStack));
    }

    private static Map<ItemAttribute, Number> readAttributes(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return new HashMap<>();
        }

        return new HashMap<>(ItemAttributes.getInstance().getAttributeManager().getAttributeValues(meta));
    }

    private static List<SlotHolder> readSlots(ItemManager manager, ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return List.of();
        }

        return manager.getSlotManager().getSlots(meta);
    }
}
