package net.pwing.itemattributes.item.renderer.replacer;

import net.kyori.adventure.text.Component;

public class ComponentReplacer implements Replacer<Component> {
    private Component component;

    public ComponentReplacer(Component component) {
        this.component = component;
    }

    @Override
    public ComponentReplacer replace(String key, Component value) {
        this.component = this.component.replaceText(builder -> builder.matchLiteral("%" + key + "%").replacement(value));
        return this;
    }

    @Override
    public Component complete() {
        return this.component;
    }
}
