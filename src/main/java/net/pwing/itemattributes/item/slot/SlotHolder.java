package net.pwing.itemattributes.item.slot;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class SlotHolder {
    private static final NamespacedKey SLOT_DISPLAY_NAME = new NamespacedKey(ItemAttributes.getInstance(), "slot_display_name");

    private final SlotType type;
    private final PersistentDataContainer container;

    public SlotHolder(SlotType type, PersistentDataContainer container) {
        this.type = type;
        this.container = container;
    }

    public final SlotType getType() {
        return this.type;
    }

    public final boolean hasDisplayName() {
        return this.container.has(SLOT_DISPLAY_NAME);
    }

    @Nullable
    public final Component getDisplayName() {
        if (this.container.has(SLOT_DISPLAY_NAME)) {
            String displayName = this.container.get(SLOT_DISPLAY_NAME, PersistentDataType.STRING);
            return MiniMessage.miniMessage().deserialize(displayName);
        }

        return null;
    }

    public final void setDisplayName(@Nullable Component displayName) {
        if (displayName == null) {
            this.container.remove(SLOT_DISPLAY_NAME);
        } else {
            String serialized = MiniMessage.miniMessage().serialize(displayName);
            this.container.set(SLOT_DISPLAY_NAME, PersistentDataType.STRING, serialized);
        }
    }

    public abstract boolean isEmpty();

    protected abstract Component renderSlotInfo(AttributableItem item);

    public abstract List<Component> describeSlotInfo();

    public abstract void applyFromRawArgs(String[] args) throws IllegalArgumentException;

    public void applyFromHolder(SlotHolder holder) {
        if (holder == null) {
            return;
        }

        if (holder.container.has(SLOT_DISPLAY_NAME)) {
            String displayName = holder.container.get(SLOT_DISPLAY_NAME, PersistentDataType.STRING);
            this.container.set(SLOT_DISPLAY_NAME, PersistentDataType.STRING, displayName);
        }
    }

    public void save() {
    }

    public Component getSlotInfo(AttributableItem item) {
        if (this.container.has(SLOT_DISPLAY_NAME)) {
            return this.getDisplayName();
        }

        return this.renderSlotInfo(item);
    }

    public PersistentDataContainer getDataContainer() {
        return this.container;
    }
}
