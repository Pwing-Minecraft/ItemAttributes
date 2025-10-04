package net.pwing.itemattributes.item.renderer;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.item.renderer.replacer.Replacer;
import net.pwing.itemattributes.item.tier.ItemTier;

public class TierRenderer implements ItemRenderer {

    @Override
    public String render(String text, AttributableItem item, Replacer<String> replacer) {
        ItemTier tier = item.getTier();
        if (tier == null) {
            return text;
        }

        return replacer.replace("tier", tier.getName())
                .replace("tier_color", tier.getColor().asHexString())
                .replace("tier_description", tier.getDescription())
                .complete();
    }

    @Override
    public Component render(Component component, AttributableItem item, Replacer<Component> replacer) {
        ItemTier tier = item.getTier();
        if (tier == null) {
            return component;
        }

        return replacer.replace("tier", Component.text(tier.getName()))
                .replace("tier_description", Component.text(tier.getDescription()))
                .complete();
    }
}
