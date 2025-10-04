package net.pwing.itemattributes.item.slot;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.ItemAttribute;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;

import java.util.List;
import java.util.Map;

public class AttributeListSlotCreator implements SlotCreator {
    static final AttributeListSlotCreator INSTANCE = new AttributeListSlotCreator();

    @Override
    public int getSlotHoldersToCreate(PersistentDataHolder dataHolder, List<SlotHolder> emptySlots) {
        Map<ItemAttribute, Number> attributeValues = ItemAttributes.getInstance().getAttributeManager().getAttributeValues(dataHolder);
        if (attributeValues.isEmpty()) {
            return 0;
        }

        List<AttributeSlotHolder> emptyAttributeSlots = emptySlots.stream()
                .filter(emptySlot -> emptySlot instanceof AttributeSlotHolder)
                .map(emptySlot -> (AttributeSlotHolder) emptySlot)
                .toList();

        // We have an empty singular attribute slot, do not attempt to occupy this slot
        if (attributeValues.size() == 1 && !emptyAttributeSlots.isEmpty()) {
            return 0;
        }

        for (SlotHolder emptySlot : emptySlots) {
            // We have a list slot holder, return true
            if (emptySlot instanceof AttributeListSlotHolder) {
                return 1;
            }

            // We have a single attribute slot holder, but also have a single attribute value here
            if (emptySlot instanceof AttributeSlotHolder && attributeValues.size() == 1) {
                return 1;
            }
        }

        return 0;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<SlotHolder> createSlotHolders(PersistentDataHolder dataHolder, PersistentDataContainer holderContainer) {
        List<AttributeSlotHolder> slotHolders = (List) AttributeSlotCreator.INSTANCE.createSlotHolders(dataHolder, holderContainer.getAdapterContext().newPersistentDataContainer());
        return List.of(new AttributeListSlotHolder(holderContainer, slotHolders));
    }
}
