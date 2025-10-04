package net.pwing.itemattributes.item.renderer;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.item.renderer.replacer.Replacer;

public class UserDataRenderer implements ItemRenderer {

    @Override
    public String render(String text, AttributableItem item, Replacer<String> replacer) {
        return replacer.replace("name", item.getName() == null ? "" : item.getName())
                .replace("description", item.getDescription() == null ? "" : item.getDescription())
                .complete();
    }

    @Override
    public Component render(Component component, AttributableItem item, Replacer<Component> replacer) {
        return replacer.replace("name", item.getName() == null ? Component.empty() : item.renderFromTemplate(item.getName()))
                .replace("description", item.getDescription() == null ? Component.empty() : item.renderFromTemplate(item.getDescription()))
                .complete();
    }
}
