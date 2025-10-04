package net.pwing.itemattributes.modifier;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.modifier.event.PlayerModifiersApplyEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public interface ItemModifier {
    ModifierType<?> getType();

    Component renderModifierInfo(AttributableItem item);

    void apply(ItemStack item, UnaryOperator<String> modifierOperator);

    @SuppressWarnings("unchecked")
    static <T extends ItemModifier> void applyModifiers(ModifierType<T> type, Player player, Consumer<T> modifierConsumer) {
        PlayerModifiersApplyEvent event = new PlayerModifiersApplyEvent(player, type);
        player.getServer().getPluginManager().callEvent(event);

        for (ItemModifier modifier : event.getModifiers()) {
            modifierConsumer.accept((T) modifier);
        }
    }
}
