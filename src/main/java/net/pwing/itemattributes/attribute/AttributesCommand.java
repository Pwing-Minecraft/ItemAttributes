package net.pwing.itemattributes.attribute;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.pwing.itemattributes.command.Argument;
import net.pwing.itemattributes.command.BaseCommandExecutor;
import net.pwing.itemattributes.command.Command;
import net.pwing.itemattributes.command.tab.AttributeKeysCompleter;
import net.pwing.itemattributes.message.Messages;
import net.pwing.itemattributes.message.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class AttributesCommand extends BaseCommandExecutor {
    private final AttributeManager manager;

    public AttributesCommand(AttributeManager manager) {
        super("attributes");

        this.manager = manager;
    }

    @Command(commands = "list", description = "List all the attributes.", permissionNode = "list")
    public void list(CommandSender sender) {
        this.sendHeader(sender);

        for (Map.Entry<String, ItemAttribute> entry : this.manager.getPlugin().getAttributesConfig().getAttributes().entrySet()) {
            ItemAttribute attribute = entry.getValue();
            String name = attribute.getName();

            TextUtils.sendMessage(sender, Component.text("- ", NamedTextColor.GRAY)
                    .append(Component.text(name + ": ", NamedTextColor.WHITE))
                    .append(Component.text("Format: ", NamedTextColor.GRAY))
                    .append(AttributeCalculator.computeOperators(attribute.getDisplay(), 0))
            );
        }
    }

    @Command(commands = "bind", description = "Bind an attribute to an item.", permissionNode = "bind")
    public void bind(Player player, @Argument(name = "attribute", description = "The key of the attribute.", tabCompleter = AttributeKeysCompleter.class) String attributeKey, @Argument(name = "value", description = "The value of the attribute.") String value) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.ATTRIBUTE_MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        ItemAttribute attribute = this.manager.getPlugin().getAttributesConfig().getAttributes().get(attributeKey);
        if (attribute == null) {
            Messages.ATTRIBUTE_NOT_FOUND.send(player, attributeKey);
            return;
        }

        Number number;
        try {
            number = attribute.getType().convert(value);
        } catch (NumberFormatException e) {
            Messages.INVALID_NUMBER.send(player, value);
            return;
        }

        this.manager.bindAttribute(heldItem, attribute, number);
        Messages.ATTRIBUTE_BOUND.send(player, attribute.getName(), number.toString());
    }

    @Command(commands = "unbind", description = "Unbind an attribute from an item.", permissionNode = "unbind")
    public void unbind(Player player, @Argument(name = "attribute", description = "The key of the attribute.", tabCompleter = AttributeKeysCompleter.class) String attributeKey) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.ATTRIBUTE_MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        ItemAttribute attribute = this.manager.getPlugin().getAttributesConfig().getAttributes().get(attributeKey);
        if (attribute == null) {
            Messages.ATTRIBUTE_NOT_FOUND.send(player, attributeKey);
            return;
        }

        if (!this.manager.unbindAttribute(heldItem, attribute)) {
            Messages.ATTRIBUTE_NOT_BOUND.send(player, attribute.getName());
            return;
        }

        Messages.ATTRIBUTE_UNBOUND.send(player, attribute.getName());
    }

    @Command(commands = "iteminfo", description = "Get information about the attributes on an item.", permissionNode = "iteminfo")
    public void itemInfo(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().isAir()) {
            Messages.ATTRIBUTE_MUST_BE_HOLDING_ITEM.send(player);
            return;
        }

        Map<ItemAttribute, Number> attributes = this.manager.getAttributeValues(heldItem.getItemMeta());
        if (attributes.isEmpty()) {
            Messages.ATTRIBUTE_HAS_NO_ATTRIBUTES.send(player);
            return;
        }

        this.sendHeader(player);
        for (Map.Entry<ItemAttribute, Number> entry : attributes.entrySet()) {
            ItemAttribute attribute = entry.getKey();
            Number value = entry.getValue();

            TextUtils.sendMessage(player, Component.text("- ", NamedTextColor.GRAY)
                    .append(Component.text(attribute.getName() + ": ", NamedTextColor.WHITE))
                    .append(AttributeCalculator.computeOperators(attribute.getDisplay(), value))
            );
        }
    }
}
