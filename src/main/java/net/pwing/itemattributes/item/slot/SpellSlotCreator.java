package net.pwing.itemattributes.item.slot;

import net.pwing.itemattributes.ItemAttributes;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;

import java.util.List;
import java.util.stream.Collectors;

public class SpellSlotCreator implements SlotCreator {
    static final SpellSlotCreator INSTANCE = new SpellSlotCreator();

    private SpellSlotCreator() {
    }

    @Override
    public int getSlotHoldersToCreate(PersistentDataHolder dataHolder, List<SlotHolder> emptySlots) {
        List<SlotHolder> holders = ItemAttributes.getInstance().getItemManager().getSlotManager().getSlotsOfType(dataHolder, SlotType.SPELL);
        return holders.isEmpty() ? 0 : 1;
    }

    @Override
    public List<SlotHolder> createSlotHolders(PersistentDataHolder dataHolder, PersistentDataContainer holderContainer) {
        List<SlotHolder> spells = ItemAttributes.getInstance().getItemManager().getSlotManager().getSlotsOfType(dataHolder, SlotType.SPELL);
        return spells.stream()
                .map(holder -> new SpellSlotHolder(holderContainer, ((SpellSlotHolder) holder).getSpell()))
                .collect(Collectors.toList());
    }
}
