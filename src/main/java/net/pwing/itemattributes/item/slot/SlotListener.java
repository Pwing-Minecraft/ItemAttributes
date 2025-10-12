package net.pwing.itemattributes.item.slot;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.feature.spell.SpellHolder;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.message.Messages;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SlotListener implements Listener {
    private final SlotManager manager;

    private final Map<UUID, Long> lastSpellChanges = new HashMap<>();

    public SlotListener(SlotManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.lastSpellChanges.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        SlotMechanism slotMechanism = manager.getItemManager().getPlugin().getPluginConfig().getSlotMechanism();
        if (slotMechanism != SlotMechanism.ANVIL) {
            return;
        }

        AnvilInventory inventory = event.getInventory();
        Player player = (Player) event.getView().getPlayer();

        ItemStack input = inventory.getItem(0);
        ItemStack combine = inventory.getItem(1);

        AttributableItem attributableItem = this.manager.combineOntoItem(input, combine, player);
        if (attributableItem != null) {
            event.setResult(attributableItem.getItemStack());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }

        SlotMechanism slotMechanism = manager.getItemManager().getPlugin().getPluginConfig().getSlotMechanism();
        if (slotMechanism == SlotMechanism.DRAG && (inventory.getType() == InventoryType.PLAYER || inventory.getType() == InventoryType.CHEST)) {
            ItemStack current = event.getCurrentItem();
            ItemStack cursor = event.getCursor();

            if (current == null || cursor == null || !this.manager.isValidCombination(current, cursor)) {
                return;
            }

            if (event.getClick() != ClickType.LEFT) {
                return;
            }

            // Cancel the event, set the original items to air, and replace the player's cursor
            event.setCancelled(true);

            AttributableItem attributableItem = this.manager.combineOntoItem(current, cursor, (Player) event.getWhoClicked());
            if (attributableItem != null) {
                ItemStack item = attributableItem.getItemStack().clone();

                event.setCancelled(true);

                event.getView().setCursor(item);
                event.setCurrentItem(null);

                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 3.0f, 1.0f);
            }

            return;
        }

        if (slotMechanism != SlotMechanism.ANVIL) {
            return;
        }

        if (event.getSlot() != 2) {
            return;
        }

        if (inventory.getType() != InventoryType.ANVIL || event.getCurrentItem() == null) {
            return;
        }

        ItemStack input = inventory.getItem(0);
        ItemStack combine = inventory.getItem(1);

        if (!this.manager.isValidCombination(input, combine)) {
            return;
        }

        // Cancel the event, set the original items to air, and replace the player's cursor
        event.setCancelled(true);

        ItemStack result = event.getCurrentItem().clone();

        event.getWhoClicked().setItemOnCursor(result);

        inventory.setItem(0, new ItemStack(Material.AIR));
        inventory.setItem(1, new ItemStack(Material.AIR));

        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 3.0f, 1.0f);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        AttributableItem attributableItem = this.manager.getItemManager().getAttributableItem(item);
        if (attributableItem == null) {
            return;
        }

        SpellHolder spell = this.getSelectedSpell(attributableItem);
        if (spell == null) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            spell.cast(event.getPlayer());
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Long lastSpellChange = this.lastSpellChanges.get(event.getPlayer().getUniqueId());
            if (lastSpellChange != null && System.currentTimeMillis() - lastSpellChange < 500) {
                return;
            }

            // Select the next
            List<SpellHolder> spells = new ArrayList<>();
            for (SlotHolder slot : attributableItem.getSlots()) {
                if (slot instanceof SpellSlotHolder spellSlot && !spellSlot.isEmpty()) {
                    spells.add(spellSlot.getSpell());
                }
            }

            if (spells.isEmpty()) {
                return;
            }

            int index = spells.indexOf(spell);
            if (index == -1) {
                return;
            }

            index++;
            if (index >= spells.size()) {
                index = 0;
            }

            SpellHolder nextSpell = spells.get(index);
            NamespacedKey key = nextSpell.getKey();

            // If the spells are the same, return, we didn't change anything
            if (nextSpell.getKey().equals(spell.getKey())) {
                return;
            }

            ItemAttributes.getInstance().getItemManager().getSlotManager().setSelectedSpell(item, key);
            this.lastSpellChanges.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());

            Messages.SLOT_SELECTED_SPELL.send(event.getPlayer(), nextSpell.getDisplayName());
        }
    }

    private SpellHolder getSelectedSpell(AttributableItem item) {
        List<SpellSlotHolder> spellSlots = new ArrayList<>();
        for (SlotHolder slot : item.getSlots()) {
            if (slot instanceof SpellSlotHolder spellSlot) {
                spellSlots.add(spellSlot);
            }
        }

        if (spellSlots.isEmpty()) {
            return null;
        }

        if (spellSlots.size() == 1) {
            return spellSlots.getFirst().getSpell();
        }

        NamespacedKey selectedSpell = ItemAttributes.getInstance().getItemManager().getSlotManager().getSelectedSpell(item.getItemStack());
        if (selectedSpell == null) {
            // Nothing selected, just return the first spell
            return spellSlots.getFirst().getSpell();
        }

        for (SpellSlotHolder spellSlot : spellSlots) {
            if (spellSlot.getSpell().getKey().equals(selectedSpell)) {
                return spellSlot.getSpell();
            }
        }

        return null;
    }
}
