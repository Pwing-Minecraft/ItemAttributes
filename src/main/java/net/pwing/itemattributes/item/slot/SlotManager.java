package net.pwing.itemattributes.item.slot;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.item.ItemManager;
import net.pwing.itemattributes.message.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SlotManager {
    private static final NamespacedKey SLOTS_KEY = new NamespacedKey(ItemAttributes.getInstance(), "slots");
    private static final NamespacedKey SLOT_TYPE_KEY = new NamespacedKey(ItemAttributes.getInstance(), "slot_type");
    private static final NamespacedKey SLOT_CREATOR_KEY = new NamespacedKey(ItemAttributes.getInstance(), "slot_creator");

    private static final NamespacedKey SELECTED_SPELL = new NamespacedKey(ItemAttributes.getInstance(), "selected_spell");

    private final ItemManager itemManager;

    public SlotManager(ItemManager itemManager) {
        this.itemManager = itemManager;

        Bukkit.getPluginManager().registerEvents(new SlotListener(this), itemManager.getPlugin());
    }

    public ItemManager getItemManager() {
        return this.itemManager;
    }

    public List<SlotHolder> getSlotsOfType(PersistentDataHolder holder, SlotType slotType) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (!container.has(SLOTS_KEY, PersistentDataType.LIST.dataContainers())) {
            return List.of();
        }

        List<SlotHolder> slotHolders = new ArrayList<>();
        List<PersistentDataContainer> slotContainers = container.get(SLOTS_KEY, PersistentDataType.LIST.dataContainers());
        for (PersistentDataContainer slotContainer : slotContainers) {
            if (!slotContainer.has(SLOT_TYPE_KEY, PersistentDataType.STRING)) {
                continue;
            }

            String slotTypeKey = slotContainer.get(SLOT_TYPE_KEY, PersistentDataType.STRING);
            if (!slotTypeKey.equalsIgnoreCase(slotType.name())) {
                continue;
            }

            SlotHolder slotHolder = slotType.createSlotHolder(slotContainer);
            slotHolders.add(slotHolder);
        }

        return slotHolders;
    }

    public List<SlotHolder> getSlots(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return List.of();
        }

        return this.getSlots(itemMeta);
    }

    public List<SlotHolder> getSlots(PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (!container.has(SLOTS_KEY, PersistentDataType.LIST.dataContainers())) {
            return List.of();
        }

        List<SlotHolder> slotHolders = new ArrayList<>();
        List<PersistentDataContainer> slotContainers = container.get(SLOTS_KEY, PersistentDataType.LIST.dataContainers());
        for (PersistentDataContainer slotContainer : slotContainers) {
            if (!slotContainer.has(SLOT_TYPE_KEY, PersistentDataType.STRING)) {
                continue;
            }

            String slotTypeKey = slotContainer.get(SLOT_TYPE_KEY, PersistentDataType.STRING);
            SlotType slotType;
            try {
                slotType = SlotType.valueOf(slotTypeKey.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                continue;
            }

            SlotHolder slotHolder = slotType.createSlotHolder(slotContainer);
            slotHolders.add(slotHolder);
        }

        return slotHolders;
    }

    public void addSlot(ItemStack itemStack, SlotType type) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        this.addSlot(itemMeta, type);
        itemStack.setItemMeta(itemMeta);
    }

    public void addSlot(PersistentDataHolder holder, SlotType type) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        List<PersistentDataContainer> containers = new ArrayList<>(container.getOrDefault(SLOTS_KEY, PersistentDataType.LIST.dataContainers(), new ArrayList<>()));

        PersistentDataContainer slotContainer = container.getAdapterContext().newPersistentDataContainer();
        slotContainer.set(SLOT_TYPE_KEY, PersistentDataType.STRING, type.name().toLowerCase(Locale.ROOT));

        containers.add(type.createSlotHolder(slotContainer).getDataContainer());
        container.set(SLOTS_KEY, PersistentDataType.LIST.dataContainers(), containers);
    }

    public boolean removeSlot(ItemStack itemStack, int slotIndex) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        boolean removed = this.removeSlot(itemMeta, slotIndex);
        if (removed) {
            itemStack.setItemMeta(itemMeta);
        }

        return removed;
    }

    public boolean removeSlot(PersistentDataHolder holder, int slotIndex) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        List<PersistentDataContainer> containers = new ArrayList<>(container.getOrDefault(SLOTS_KEY, PersistentDataType.LIST.dataContainers(), new ArrayList<>()));
        if (slotIndex < 0 || slotIndex >= containers.size()) {
            return false;
        }

        containers.remove(slotIndex);
        container.set(SLOTS_KEY, PersistentDataType.LIST.dataContainers(), containers);
        return true;
    }

    public void saveSlots(ItemStack itemStack, List<SlotHolder> slotHolders) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        this.saveSlots(itemMeta, slotHolders);
        itemStack.setItemMeta(itemMeta);
    }

    public void saveSlots(PersistentDataHolder holder, List<SlotHolder> slotHolders) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        List<PersistentDataContainer> containers = new ArrayList<>();
        for (SlotHolder slotHolder : slotHolders) {
            containers.add(slotHolder.getDataContainer());
        }

        container.set(SLOTS_KEY, PersistentDataType.LIST.dataContainers(), containers);
    }

    public boolean isSlotCreator(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        return this.isSlotCreator(itemMeta);
    }

    public boolean isSlotCreator(PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        return container.has(SLOT_CREATOR_KEY, PersistentDataType.BOOLEAN);
    }

    public boolean isSlotCreator(PersistentDataContainer container) {
        return container.has(SLOT_CREATOR_KEY, PersistentDataType.BOOLEAN);
    }

    public void bindSlotCreator(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        this.bindSlotCreator(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public void bindSlotCreator(PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        container.set(SLOT_CREATOR_KEY, PersistentDataType.BOOLEAN, true);
    }

    public void bindSlotCreator(PersistentDataContainer container) {
        container.set(SLOT_CREATOR_KEY, PersistentDataType.BOOLEAN, true);
    }

    public void unbindSlotCreator(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        this.unbindSlotCreator(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public void unbindSlotCreator(PersistentDataHolder holder) {
        holder.getPersistentDataContainer().remove(SLOT_CREATOR_KEY);
    }

    public void unbindSlotCreator(PersistentDataContainer container) {
        container.remove(SLOT_CREATOR_KEY);
    }

    boolean isValidCombination(ItemStack itemStack, ItemStack slotCreator) {
        return this.combineOntoItem(itemStack, slotCreator, true, null) != null;
    }

    NamespacedKey getSelectedSpell(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(SELECTED_SPELL, PersistentDataType.STRING)) {
            return null;
        }

        String spellKey = container.get(SELECTED_SPELL, PersistentDataType.STRING);
        return NamespacedKey.fromString(spellKey);
    }

    void setSelectedSpell(ItemStack itemStack, NamespacedKey spellKey) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (spellKey == null) {
            container.remove(SELECTED_SPELL);
        } else {
            container.set(SELECTED_SPELL, PersistentDataType.STRING, spellKey.toString());
        }

        itemStack.setItemMeta(itemMeta);
    }

    AttributableItem combineOntoItem(ItemStack itemStack, ItemStack slotCreator, @Nullable Player viewer) {
        return this.combineOntoItem(itemStack, slotCreator, false, viewer);
    }

    @Nullable
    private AttributableItem combineOntoItem(ItemStack itemStack, ItemStack slotCreator, boolean validation, @Nullable Player viewer) {
        if (itemStack == null || itemStack.getType().isAir() || itemStack.getItemMeta() == null || slotCreator == null || slotCreator.getType().isAir() || slotCreator.getItemMeta() == null) {
            return null;
        }

        if (slotCreator.getAmount() != 1) {
            return null;
        }

        AttributableItem attributableItem = this.itemManager.getAttributableItem(itemStack);

        List<SlotHolder> slots = attributableItem.getSlots();
        if (slots.isEmpty() || !this.isSlotCreator(slotCreator)) {
            return null;
        }

        // First step: Check that we have any slots available at all
        Map<SlotType, List<SlotHolder>> emptySlots = new HashMap<>();
        for (SlotHolder slot : slots) {
            if (slot.isEmpty()) {
                emptySlots.computeIfAbsent(slot.getType(), e -> new ArrayList<>()).add(slot);
            }
        }

        // Fast track: Ensure we have *at least one* slot available
        if (emptySlots.isEmpty()) {
            return null;
        }

        // Second step: Check what that the creator is able to occupy the slots needed
        Map<SlotType, List<SlotHolder>> slotsToCreate = new HashMap<>();
        List<SlotHolder> allEmptySlots = emptySlots.values().stream().flatMap(List::stream).toList();
        for (SlotType type : SlotType.values()) {
            int availableSlots = type.getSlotHoldersToCreate(slotCreator.getItemMeta(), allEmptySlots);
            if (availableSlots > 0) {
                slotsToCreate.put(type, type.createNewSlotHolders(slotCreator.getItemMeta(), attributableItem.getItemStack().getItemMeta().getPersistentDataContainer()));
            }
        }

        // Second (and a half step): Sanitize the values we have - deal with special cases (i.e. attribute & attribute list)
        if (slotsToCreate.isEmpty()) {
            return null; // No slots available
        }

        // Third step: Validate that we have all the needed slots
        for (Map.Entry<SlotType, List<SlotHolder>> entry : slotsToCreate.entrySet()) {
            List<SlotHolder> emptySlotHolders = emptySlots.get(entry.getKey());
            if (emptySlotHolders == null || emptySlotHolders.isEmpty()) {
                return null; // No available slots for our type
            }

            // Check that we have enough empty slots of our type
            if (emptySlotHolders.size() < entry.getValue().size()) {
                return null; // Not enough empty slots
            }
        }

        // Success! We have available slots!

        // If we are just validating the result, don't bother re-rendering or
        // applying anything, since we just want to check if it is valid
        if (validation) {
            return attributableItem;
        }

        // Fourth (and final) step: Find and occupy the empty slots

        for (Map.Entry<SlotType, List<SlotHolder>> entry : slotsToCreate.entrySet()) {
            List<SlotHolder> emptySlotHolders = emptySlots.get(entry.getKey());
            for (int i = 0; i < entry.getValue().size(); i++) {
                SlotHolder creatorSlot = entry.getValue().get(i);
                SlotHolder slotToOccupy = emptySlotHolders.get(i);

                Map<String, Boolean> copySlotName = this.itemManager.getPlugin().getPluginConfig().getCopySlotName();
                Boolean copySlotNameOption;
                if (!creatorSlot.hasDisplayName() && (copySlotNameOption = copySlotName.get(creatorSlot.getType().name().toLowerCase(Locale.ROOT))) != null && copySlotNameOption) {
                    slotToOccupy.setDisplayName(TextUtils.fromLegacy(slotCreator.getItemMeta().getDisplayName()));
                }

                // Phew, I think we're good now
                slotToOccupy.applyFromHolder(creatorSlot);
                slotToOccupy.save();
            }
        }

        this.saveSlots(attributableItem.getItemStack(), attributableItem.getSlots());

        // Re-apply attributes from the new slot info
        attributableItem.reapplyAttributes();

        // Re-render our attributable item
        attributableItem.render(viewer);

        return attributableItem;
    }
}
