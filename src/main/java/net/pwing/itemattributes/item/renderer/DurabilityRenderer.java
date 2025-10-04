package net.pwing.itemattributes.item.renderer;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.item.renderer.replacer.Replacer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DurabilityRenderer implements ItemRenderer {

    @Override
    public String render(String text, AttributableItem item, Replacer<String> replacer) {
        ItemStack itemStack = item.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return text;
        }

        if (!(meta instanceof Damageable damageable)) {
            return text;
        }

        int maxDamage = damageable.hasMaxDamage() ? damageable.getMaxDamage() : itemStack.getType().getMaxDurability();
        return replacer.replace("durability", String.valueOf(maxDamage - damageable.getDamage()))
                .replace("max_durability", String.valueOf(maxDamage))
                .complete();
    }

    @Override
    public Component render(Component component, AttributableItem item, Replacer<Component> replacer) {
        ItemStack itemStack = item.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return component;
        }

        if (!(meta instanceof Damageable damageable)) {
            return component;
        }

        int maxDamage = damageable.hasMaxDamage() ? damageable.getMaxDamage() : itemStack.getType().getMaxDurability();
        return replacer.replace("durability", Component.text(maxDamage - damageable.getDamage()))
                .replace("max_durability", Component.text(maxDamage))
                .complete();
    }
}
