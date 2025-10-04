package net.pwing.itemattributes.attribute;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.event.AttributeUpdateEvent;
import net.pwing.itemattributes.attribute.event.ItemAttributeUpdateEvent;
import net.pwing.itemattributes.attribute.event.PlayerAttributeApplyEvent;
import net.pwing.itemattributes.attribute.event.PlayerAttributeResetEvent;
import net.pwing.itemattributes.item.slot.AttributeListSlotHolder;
import net.pwing.itemattributes.item.slot.AttributeSlotHolder;
import net.pwing.itemattributes.item.slot.SlotHolder;
import net.pwing.itemattributes.item.slot.SlotManager;
import net.pwing.itemattributes.item.slot.SlotType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AttributeManager {
    private static final NamespacedKey ATTRIBUTES_KEY = new NamespacedKey(ItemAttributes.getInstance(), "attributes");
    
    private final ItemAttributes plugin;

    public AttributeManager(ItemAttributes plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new AttributeListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new AttributeEventListener(this), plugin);
    }

    public void refreshAttributes(Player player, ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        List<ItemAttribute> attributes = this.getAttributes(item.getItemMeta());
        for (ItemAttribute attribute : attributes) {
            this.refreshAttributes(player, attribute);
        }
    }

    public void resetAttributes(Player player, ItemStack item) {
        List<ItemAttribute> attributes = this.getAttributes(item.getItemMeta());
        for (ItemAttribute attribute : attributes) {
            this.resetAttributes(player, attribute);
        }
    }

    public void refreshAttributes(Player player, ItemAttribute attribute) {
        Number value = AttributeCalculator.calculateFullAttributeValue(player, this, attribute);

        PlayerAttributeApplyEvent event = new PlayerAttributeApplyEvent(player, attribute, value);
        Bukkit.getPluginManager().callEvent(event);

        attribute.getAttribute().apply(player, event.getValue());
    }

    public void resetAttributes(Player player, ItemAttribute attribute) {
        PlayerAttributeResetEvent event = new PlayerAttributeResetEvent(player, attribute, this.getAttributeValue(player, attribute));
        Bukkit.getPluginManager().callEvent(event);

        attribute.getAttribute().reset(player);
    }

    public void bindAttribute(ItemStack itemStack, ItemAttribute attribute, Number value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        this.bindAttribute(meta, attribute, value, false);

        itemStack.setItemMeta(meta);

        ItemAttributeUpdateEvent event = new ItemAttributeUpdateEvent(attribute, itemStack, value);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void bindAttribute(PersistentDataHolder holder, ItemAttribute attribute, Number value) {
        this.bindAttribute(holder, attribute, value, true);
    }

    private void bindAttribute(PersistentDataHolder holder, ItemAttribute attribute, Number value, boolean callEvent) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        
        PersistentDataContainer attributesContainer = container.getOrDefault(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
        this.bindAttribute(attributesContainer, attribute, value);

        container.set(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER, attributesContainer);

        if (callEvent) {
            AttributeUpdateEvent event = new AttributeUpdateEvent(attribute, holder, value);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public void bindAttribute(PersistentDataContainer container, ItemAttribute attribute, Number value) {
        container.set(attribute.getKey(), attribute.getType().getStorageType(), attribute.getType().convert(value));
    }

    public boolean unbindAttribute(ItemStack itemStack, ItemAttribute attribute) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }

        boolean unbound = this.unbindAttribute(meta, attribute, false);
        if (!unbound) {
            return false;
        }

        itemStack.setItemMeta(meta);

        ItemAttributeUpdateEvent event = new ItemAttributeUpdateEvent(attribute, itemStack, 0);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }

    public boolean unbindAttribute(PersistentDataHolder holder, ItemAttribute attribute) {
        return this.unbindAttribute(holder, attribute, true);
    }

    private boolean unbindAttribute(PersistentDataHolder holder, ItemAttribute attribute, boolean callEvent) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (!container.has(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER)) {
            return false;
        }

        PersistentDataContainer attributesContainer = container.get(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER);
        if (!attributesContainer.has(attribute.getKey(), attribute.getType().getStorageType())) {
            return false;
        }

        attributesContainer.remove(attribute.getKey());
        container.set(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER, attributesContainer);

        if (callEvent) {
            AttributeUpdateEvent event = new AttributeUpdateEvent(attribute, holder, 0);
            Bukkit.getPluginManager().callEvent(event);
        }

        return true;
    }

    public List<ItemAttribute> getAttributes(PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (!container.has(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER)) {
            return List.of();
        }

        List<ItemAttribute> attributes = new ArrayList<>();
        PersistentDataContainer attributesContainer = container.get(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER);
        for (NamespacedKey key : attributesContainer.getKeys()) {
            ItemAttribute attribute = this.plugin.getAttributesConfig().getAttributes().get(key.getKey());
            if (attribute == null) {
                continue;
            }

            attributes.add(attribute);
        }

        return attributes;
    }

    public Map<ItemAttribute, Number> getAttributeValues(PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (!container.has(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER)) {
            return Map.of();
        }

        Map<ItemAttribute, Number> attributes = new HashMap<>();
        PersistentDataContainer attributesContainer = container.get(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER);
        Set<NamespacedKey> attributeTypes = new HashSet<>(attributesContainer.getKeys());

        SlotManager slotManager = this.plugin.getItemManager().getSlotManager();
        for (SlotHolder slotHolder : slotManager.getSlotsOfType(holder, SlotType.ATTRIBUTE)) {
            if (slotHolder.isEmpty() || !(slotHolder instanceof AttributeSlotHolder attributeHolder)) {
                continue;
            }

            attributeTypes.add(attributeHolder.getAttribute().getKey());
        }

        for (SlotHolder listSlotHolder : slotManager.getSlotsOfType(holder, SlotType.ATTRIBUTE_LIST)) {
            if (listSlotHolder.isEmpty() || !(listSlotHolder instanceof AttributeListSlotHolder attributeListHolder)) {
                continue;
            }

            for (AttributeSlotHolder slotHolder : attributeListHolder.getSlots()) {
                if (slotHolder.isEmpty()) {
                    continue;
                }

                attributeTypes.add(slotHolder.getAttribute().getKey());
            }
        }

        for (NamespacedKey key : attributeTypes) {
            ItemAttribute attribute = this.plugin.getAttributesConfig().getAttributes().get(key.getKey());
            if (attribute == null) {
                continue;
            }

            attributes.put(attribute, this.getAttributeValue(holder, attribute));
        }

        return attributes;
    }

    public Number getAttributeValue(PersistentDataHolder holder, ItemAttribute attribute) {
        return this.attributeValue(holder, attribute).orElse(0);
    }

    public Optional<Number> attributeValue(PersistentDataHolder holder, ItemAttribute attribute) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (!container.has(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER)) {
            return Optional.empty();
        }

        PersistentDataContainer attributesContainer = container.get(ATTRIBUTES_KEY, PersistentDataType.TAG_CONTAINER);
        Number baseValue = this.attributeValue(attributesContainer, attribute).orElse(0);

        // See if we have any attributes in additional slots bound to the item
        SlotManager slotManager = this.plugin.getItemManager().getSlotManager();
        for (SlotHolder slotHolder : slotManager.getSlotsOfType(holder, SlotType.ATTRIBUTE)) {
            if (slotHolder.isEmpty() || !(slotHolder instanceof AttributeSlotHolder attributeHolder) || attributeHolder.getAttribute() != attribute) {
                continue;
            }

           baseValue = baseValue.doubleValue() + attributeHolder.getValue().doubleValue();
        }

        for (SlotHolder listSlotHolder : slotManager.getSlotsOfType(holder, SlotType.ATTRIBUTE_LIST)) {
            if (listSlotHolder.isEmpty() || !(listSlotHolder instanceof AttributeListSlotHolder attributeListHolder)) {
                continue;
            }

            for (AttributeSlotHolder slotHolder : attributeListHolder.getSlots()) {
                if (slotHolder.isEmpty() || slotHolder.getAttribute() != attribute) {
                    continue;
                }

                baseValue = baseValue.doubleValue() + slotHolder.getValue().doubleValue();
            }
        }

        return Optional.of(attribute.getType().convert(baseValue));
    }

    public Number getAttributeValue(PersistentDataContainer container, ItemAttribute attribute) {
        return this.attributeValue(container, attribute).orElse(0);
    }

    public Optional<Number> attributeValue(PersistentDataContainer container, ItemAttribute attribute) {
        if (!container.has(attribute.getKey(), attribute.getType().getStorageType())) {
            return Optional.empty();
        }

        return Optional.of((Number) container.get(attribute.getKey(), attribute.getType().getStorageType()));
    }

    public ItemAttributes getPlugin() {
        return this.plugin;
    }
}
