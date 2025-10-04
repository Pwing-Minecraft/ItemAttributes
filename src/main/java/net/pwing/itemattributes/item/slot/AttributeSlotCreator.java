package net.pwing.itemattributes.item.slot;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.ItemAttribute;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeSlotCreator implements SlotCreator {
    static final AttributeSlotCreator INSTANCE = new AttributeSlotCreator();

    private AttributeSlotCreator() {
    }

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

        if (emptyAttributeSlots.isEmpty()) {
            return 0;
        }

        // If we need more slots than are available, return 0
        if (attributeValues.size() > emptyAttributeSlots.size()) {
            return 0;
        }

        return attributeValues.size();
    }

    @Override
    public List<SlotHolder> createSlotHolders(PersistentDataHolder dataHolder, PersistentDataContainer holderContainer) {
        Map<ItemAttribute, Number> attributeValues = ItemAttributes.getInstance().getAttributeManager().getAttributeValues(dataHolder);
        List<SlotHolder> holders = new ArrayList<>(attributeValues.size());
        for (Map.Entry<ItemAttribute, Number> entry : attributeValues.entrySet()) {
            holders.add(new AttributeSlotHolder(holderContainer, entry.getKey(), entry.getValue()));
        }

        return holders;
    }
}
