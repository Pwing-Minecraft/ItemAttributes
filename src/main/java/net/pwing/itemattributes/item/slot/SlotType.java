package net.pwing.itemattributes.item.slot;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;

import java.util.List;
import java.util.function.Function;

public enum SlotType {
    ATTRIBUTE(AttributeSlotHolder::new, AttributeSlotCreator.INSTANCE),
    ATTRIBUTE_LIST(AttributeListSlotHolder::new, AttributeListSlotCreator.INSTANCE),
    SPELL(SpellSlotHolder::new, SpellSlotCreator.INSTANCE);

    private final Function<PersistentDataContainer, SlotHolder> instanceCreator;
    private final SlotCreator slotCreator;

    SlotType(Function<PersistentDataContainer, SlotHolder> instanceCreator, SlotCreator slotCreator) {
        this.instanceCreator = instanceCreator;
        this.slotCreator = slotCreator;
    }

    public SlotHolder createSlotHolder(PersistentDataContainer container) {
        return this.instanceCreator.apply(container);
    }

    public int getSlotHoldersToCreate(PersistentDataHolder dataHolder, List<SlotHolder> emptySlots) {
        return this.slotCreator == null ? 0 : this.slotCreator.getSlotHoldersToCreate(dataHolder, emptySlots);
    }

    public List<SlotHolder> createNewSlotHolders(PersistentDataHolder dataHolder, PersistentDataContainer holderContainer) {
        return this.slotCreator.createSlotHolders(dataHolder, holderContainer);
    }
}
