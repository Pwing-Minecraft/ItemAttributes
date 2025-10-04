package net.pwing.itemattributes.util;

import net.kyori.adventure.text.Component;

public class RenderableComponent<C> {
    private final String template;
    private final Renderer<C> renderer;

    public RenderableComponent(String template, Renderer<C> renderer) {
        this.template = template;
        this.renderer = renderer;
    }

    public Component render(C context) {
        return this.renderer.render(context, this.template);
    }

    public interface Renderer<C> {

        Component render(C context, String template);
    }
}
