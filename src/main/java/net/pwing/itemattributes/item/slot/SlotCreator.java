package net.pwing.itemattributes.item.slot;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;

import java.util.List;

public interface SlotCreator {

    int getSlotHoldersToCreate(PersistentDataHolder dataHolder, List<SlotHolder> emptySlots);

    List<SlotHolder> createSlotHolders(PersistentDataHolder dataHolder, PersistentDataContainer holderContainer);
}
