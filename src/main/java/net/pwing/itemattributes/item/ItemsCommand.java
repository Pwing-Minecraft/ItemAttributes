package net.pwing.itemattributes.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pwing.itemattributes.command.Argument;
import net.pwing.itemattributes.command.BaseCommandExecutor;
import net.pwing.itemattributes.command.Command;
import net.pwing.itemattributes.command.tab.ItemTemplatesCompleter;
import net.pwing.itemattributes.command.tab.ItemTiersCompleter;
import net.pwing.itemattributes.item.slot.SlotHolder;
import net.pwing.itemattributes.item.slot.SlotManager;
import net.pwing.itemattributes.item.slot.SlotType;
import net.pwing.itemattributes.item.tier.ItemTier;
import net.pwing.itemattributes.message.Messages;
import net.pwing.itemattributes.message.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

public class ItemsCommand extends BaseCommandExecutor {
    private final ItemManager manager;

    public ItemsCommand(ItemManager manager) {
        super("items");

        this.manager = manager;
    }

    @Command(commands = "reload", description = "Reload the plugin.", permissionNode = "reload")
    public void reload(CommandSender sender) {
        this.manager.getPlugin().reload();

        Messages.PLUGIN_RELOADED.send(sender);
    }

    @Command(commands = "template", subCommands = "bind", description = "Bind a template to an item.", permissionNode = "template.bind")
    public void bind(Player player, @Argument(name = "template", description = "The key of the template", tabCompleter = ItemTemplatesCompleter.class) String templateKey) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.TEMPLATE_MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        ItemTemplate template = this.manager.getPlugin().getItemTemplateConfig().getItemTemplates().get(templateKey);
        if (template == null) {
            Messages.TEMPLATE_NOT_FOUND.send(player, templateKey);
            return;
        }

        this.manager.bindTemplate(heldItem, template);
        this.manager.renderItem(heldItem, player);

        Messages.TEMPLATE_BOUND.send(player, template.getId());
    }

    @Command(commands = "tier", subCommands = "bind", description = "Bind a tier to an item.", permissionNode = "tier.bind")
    public void bindTier(Player player, @Argument(name = "tier", description = "The key of the tier", tabCompleter = ItemTiersCompleter.class) String tierKey) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.TIER_MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        ItemTier tier = this.manager.getPlugin().getTierConfig().getTiers().get(tierKey);
        if (tier == null) {
            Messages.TIER_NOT_FOUND.send(player, tierKey);
            return;
        }

        this.manager.bindTier(heldItem, tier);
        this.manager.renderItem(heldItem, player);

        Messages.TIER_BOUND.send(player, tier.getName());
    }

    @Command(commands = "name", description = "Set the name of an item.", permissionNode = "name")
    public void setName(Player player, @Argument(name = "name", description = "The name of the item") String[] name) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        String fullName = String.join(" ", name);
        if (fullName.equals("clear")) {
            this.manager.setName(heldItem, null);

            Messages.NAME_CLEARED.send(player);
        } else {
            this.manager.setName(heldItem, fullName);

            Messages.NAME_SET.send(player, MiniMessage.miniMessage().deserialize(fullName));
        }

        this.manager.renderItem(heldItem, player);
    }

    @Command(commands = "description", description = "Set the description of an item.", permissionNode = "description")
    public void setDescription(Player player, @Argument(name = "description", description = "The description of the item") String[] description) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        String fullDescription = String.join(" ", description);
        if (fullDescription.equals("clear")) {
            this.manager.setDescription(heldItem, null);

            Messages.DESCRIPTION_CLEARED.send(player);
        } else {
            this.manager.setDescription(heldItem, fullDescription);

            Messages.DESCRIPTION_SET.send(player, MiniMessage.miniMessage().deserialize(fullDescription));
        }

        this.manager.renderItem(heldItem, player);
    }

    @Command(commands = "slot", subCommands = "add", description = "Add a slot to an item.", permissionNode = "slot.add")
    public void addSlot(Player player, @Argument(name = "slot", description = "The type of slot") SlotType type) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        SlotManager slotManager = this.manager.getSlotManager();
        slotManager.addSlot(heldItem, type);

        // Re-render the item after updating the slot
        this.manager.renderItem(heldItem, player);

        Messages.SLOT_BOUND.send(player, type.name().toLowerCase(Locale.ROOT));
    }

    @Command(commands = "slot", subCommands = "bind", description = "Bind something to an existing slot.", permissionNode = "slot.bind")
    public void bindSlot(Player player, @Argument(name = "slot", description = "The type of slot") SlotType type, @Argument(name = "value", description = "What to bind into the slot.") String[] value) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        SlotManager slotManager = this.manager.getSlotManager();
        List<SlotHolder> allHolders = slotManager.getSlots(heldItem);
        List<SlotHolder> holders = allHolders.stream()
                .filter(holder -> holder.isEmpty() && holder.getType() == type)
                .toList();

        if (holders.isEmpty()) {
            Messages.SLOT_OF_TYPE_NOT_FOUND.send(player, type.name().toLowerCase(Locale.ROOT));
            return;
        }

        SlotHolder holder = holders.getFirst();
        try {
            holder.applyFromRawArgs(value);
        } catch (IllegalArgumentException e) {
            Messages.SLOT_BINDING_FAILED.send(player, type.name().toLowerCase(Locale.ROOT), e.getMessage());
            return;
        }

        // Save the slot holder to the item
        holder.save();

        slotManager.saveSlots(heldItem, allHolders);

        // Re-render the item after updating the slot
        this.manager.renderItem(heldItem, player);

        Messages.SLOT_BOUND.send(player, type.name().toLowerCase(Locale.ROOT));
    }

    @Command(commands = "slot", subCommands = "remove", description = "Remove a slot from an item.", permissionNode = "slot.remove")
    public void removeSlot(Player player, @Argument(name = "slot index", description = "The index of the slot") int slotIndex) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        int originalSlotIndex = slotIndex;

        // User facing indexes start at 1
        slotIndex--;

        SlotManager slotManager = this.manager.getSlotManager();
        if (slotManager.removeSlot(heldItem, slotIndex)) {
            Messages.SLOT_REMOVED.send(player, Integer.toString(originalSlotIndex));

            // Re-render the item after updating the slot
            this.manager.renderItem(heldItem, player);
        } else {
            Messages.SLOT_INDEX_NOT_FOUND.send(player, Integer.toString(originalSlotIndex));
        }
    }

    @Command(commands = "slot", subCommands = "creator", description = "Make an item a slot creator.", permissionNode = "slot.creator")
    public void slotCreator(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        SlotManager slotManager = this.manager.getSlotManager();
        if (slotManager.isSlotCreator(heldItem)) {
            slotManager.unbindSlotCreator(heldItem);

            Messages.SLOT_CREATOR_UNBOUND.send(player);
        } else {
            slotManager.bindSlotCreator(heldItem);

            Messages.SLOT_CREATOR_BOUND.send(player);
        }
    }

    @Command(commands = "slot", subCommands = "info", description = "See information about the occupied slots for your item.", permissionNode = "slot.info")
    public void slotInfo(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        SlotManager slotManager = this.manager.getSlotManager();
        List<SlotHolder> slots = slotManager.getSlots(heldItem);
        if (slots.isEmpty()) {
            Messages.SLOT_HAS_NO_SLOTS.send(player);
            return;
        }

        for (int i = 0; i < slots.size(); i++) {
            SlotHolder slot = slots.get(i);
            Messages.SLOT_NUMBER.send(player, Integer.toString(i + 1));

            if (slot.isEmpty()) {
                TextUtils.sendMessage(player, Component.text("- ", NamedTextColor.GRAY).append(Messages.SLOT_EMPTY.toComponent(player)));
                continue;
            }

            if (slot.hasDisplayName()) {
                TextUtils.sendMessage(player, Component.text("- ", NamedTextColor.GRAY).append(Messages.SLOT_NAME.toComponent(player, slot.getDisplayName())));
            }

            for (Component component : slot.describeSlotInfo()) {
                TextUtils.sendMessage(player, Component.text("- ", NamedTextColor.GRAY).append(component));
            }
        }
    }
}
