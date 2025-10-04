package net.pwing.itemattributes.armor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Lectern;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;

public class ArmorChangeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        if (action == InventoryAction.NOTHING) {
            return;
        }

        InventoryType.SlotType slotType = event.getSlotType();
        if (slotType != InventoryType.SlotType.ARMOR && slotType != InventoryType.SlotType.QUICKBAR && slotType != InventoryType.SlotType.CONTAINER) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getType() != InventoryType.PLAYER) {
            return;
        }

        if (event.getInventory().getType() != InventoryType.CRAFTING && event.getInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        ClickType clickType = event.getClick();

        boolean shiftClick = clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT;
        boolean hotkey = clickType == ClickType.NUMBER_KEY || clickType == ClickType.SWAP_OFFHAND;

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        EquipmentSlot slot = matchSlot(shiftClick ? currentItem : cursor);
        if (!shiftClick && slot != null && event.getRawSlot() != getArmorSlot(slot)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        PlayerInventory inventory = player.getInventory();
        if (shiftClick) {
            slot = matchSlot(currentItem);
            if (slot != null) {
                int armorSlot = getArmorSlot(slot);
                boolean equipping = event.getRawSlot() != armorSlot;
                if ((slot == EquipmentSlot.HEAD && (equipping == isEmpty(inventory.getHelmet())))
                        || (slot == EquipmentSlot.CHEST && (equipping == isEmpty(inventory.getChestplate())))
                        || (slot == EquipmentSlot.LEGS && (equipping == isEmpty(inventory.getLeggings())))
                        || (slot == EquipmentSlot.FEET && (equipping == isEmpty(inventory.getBoots())))) {

                    PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(player, slot, equipping ? null : currentItem, equipping ? currentItem : null);
                    Bukkit.getPluginManager().callEvent(armorEvent);
                    if (armorEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            ItemStack newPiece = cursor;
            ItemStack oldPiece = currentItem;
            if (hotkey) {
                if (clickedInventory.getType() == InventoryType.PLAYER) {
                    ItemStack hotbarItem = null;
                    if (event.getHotbarButton() != -1) {
                        hotbarItem = clickedInventory.getItem(event.getHotbarButton());
                    } else if (event.getHotbarButton() == -1 && clickedInventory instanceof PlayerInventory playerInventory) {
                        hotbarItem = playerInventory.getItemInOffHand();
                    }

                    if (!isEmpty(hotbarItem)) {
                        slot = matchSlot(hotbarItem);
                        newPiece = hotbarItem;
                        oldPiece = clickedInventory.getItem(event.getSlot());
                    } else {
                        slot = matchSlot(!isEmpty(currentItem) ? currentItem : cursor);
                    }
                }
            } else {
                if (isEmpty(cursor) && !isEmpty(currentItem)) {
                    slot = matchSlot(currentItem);
                }
            }

            if (slot != null && event.getRawSlot() == getArmorSlot(slot)) {
                PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(player, slot, oldPiece, newPiece);
                Bukkit.getPluginManager().callEvent(armorEvent);
                if (armorEvent.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) {
            return;
        }

        if (event.getAction() == Action.PHYSICAL) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (event.useInteractedBlock() != Event.Result.DENY) {
            if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) {
                if (isInteractable(event.getClickedBlock())) {
                    return;
                }
            }
        }

        EquipmentSlot slot = matchSlot(event.getItem());

        // Edge case for carved pumpkins
        if (event.getItem() != null && event.getItem().getType() == Material.CARVED_PUMPKIN) {
            return;
        }

        if (slot == null) {
            return;
        }

        ItemStack oldPiece = null;
        switch (slot) {
            case HEAD -> oldPiece = player.getInventory().getHelmet();
            case CHEST -> oldPiece = player.getInventory().getChestplate();
            case LEGS -> oldPiece = player.getInventory().getLeggings();
            case FEET -> oldPiece = player.getInventory().getBoots();
        }

        EquipmentSlot oldSlot = matchSlot(oldPiece);
        if (isEmpty(oldPiece) || slot == oldSlot) {
            PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(player, slot, oldPiece, event.getItem() == null ? null : event.getItem());
            Bukkit.getPluginManager().callEvent(armorEvent);
            if (armorEvent.isCancelled()) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (event.getRawSlots().isEmpty()) {
            return;
        }

        EquipmentSlot slot = matchSlot(event.getOldCursor());
        if (slot != null && getArmorSlot(slot) == event.getRawSlots().stream().findFirst().orElse(0)) {
            PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent((Player) event.getWhoClicked(), slot, null, event.getOldCursor());
            Bukkit.getPluginManager().callEvent(armorEvent);
            if (armorEvent.isCancelled()) {
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        EquipmentSlot slot = matchSlot(event.getBrokenItem());
        if (slot == null) {
            return;
        }

        Player player = event.getPlayer();
        PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(player, slot, event.getBrokenItem(), null);
        Bukkit.getPluginManager().callEvent(armorEvent);
        if (armorEvent.isCancelled()) {
            ItemStack brokenItem = event.getBrokenItem().clone();
            brokenItem.setAmount(1);
            ItemMeta meta = brokenItem.getItemMeta();
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damageable.getDamage() - 1);
            }
            brokenItem.setItemMeta(meta);
            if (slot == EquipmentSlot.HEAD) {
                player.getInventory().setHelmet(brokenItem);
            } else if (slot == EquipmentSlot.CHEST) {
                player.getInventory().setChestplate(brokenItem);
            } else if (slot == EquipmentSlot.LEGS) {
                player.getInventory().setLeggings(brokenItem);
            } else if (slot == EquipmentSlot.FEET) {
                player.getInventory().setBoots(brokenItem);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) {
            return;
        }

        Player player = event.getEntity();
        for (ItemStack content : player.getInventory().getArmorContents()) {
            if (!isEmpty(content)) {
                PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(player, matchSlot(content), content, null);
                Bukkit.getPluginManager().callEvent(armorEvent);
                // Cannot cancel
            }
        }
    }

    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
        EquipmentSlot slot = matchSlot(event.getItem());
        if (slot == null) {
            return;
        }

        if (!(event.getTargetEntity() instanceof Player player)) {
            return;
        }

        PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(player, slot, null, event.getItem());
        Bukkit.getPluginManager().callEvent(armorEvent);

        if (armorEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    private static boolean isEmpty(ItemStack item) {
        return item == null || item.getType().isAir() || item.getAmount() == 0;
    }

    private static EquipmentSlot matchSlot(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }

        // Check if the item is able to be equipped by other means
        if (!itemMeta.hasEquippable()) {
            if (Tag.ITEMS_HEAD_ARMOR.isTagged(itemStack.getType()) || itemStack.getType() == Material.CARVED_PUMPKIN) {
                return EquipmentSlot.HEAD;
            } else if (Tag.ITEMS_CHEST_ARMOR.isTagged(itemStack.getType())) {
                return EquipmentSlot.CHEST;
            } else if (Tag.ITEMS_LEG_ARMOR.isTagged(itemStack.getType())) {
                return EquipmentSlot.LEGS;
            } else if (Tag.ITEMS_FOOT_ARMOR.isTagged(itemStack.getType())) {
                return EquipmentSlot.FEET;
            }

            return null;
        }

        EquippableComponent equippable = itemMeta.getEquippable();
        return equippable.getSlot();
    }

    private static int getArmorSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 5;
            case CHEST -> 6;
            case LEGS -> 7;
            case FEET -> 8;
            default -> throw new IllegalArgumentException("Invalid slot: " + slot);
        };
    }

    private static boolean isInteractable(Block block) {
        BlockState state = block.getState();
        // Containers (Chests, Furnaces, Shulker Boxes, Hoppers)
        if (state instanceof Container || state instanceof Lectern || state instanceof Sign) {
            return true;
        }

        // Doors, Trapdoors, Fence Gates (Openable)
        if (block.getBlockData() instanceof Openable) {
            return true;
        }

        // Buttons, Levers (Switch)
        if (block.getBlockData() instanceof Switch) {
            return true;
        }

        return switch (block.getType()) {
            case BELL, STONECUTTER, GRINDSTONE, LOOM, CARTOGRAPHY_TABLE, SMOKER, BLAST_FURNACE, COMPOSTER,
                 DAYLIGHT_DETECTOR, CAULDRON, LAVA_CAULDRON, WATER_CAULDRON, POWDER_SNOW_CAULDRON -> true;
            default -> false;
        };
    }
}
