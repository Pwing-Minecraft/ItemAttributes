package net.pwing.itemattributes.item.renderer;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.item.renderer.replacer.Replacer;

import java.util.List;

public interface ItemRenderer {
    List<ItemRenderer> RENDERERS = List.of(
            new DurabilityRenderer(),
            new TierRenderer(),
            new UserDataRenderer()
    );

    String render(String text, AttributableItem item, Replacer<String> replacer);

    Component render(Component component, AttributableItem item, Replacer<Component> replacer);
}
