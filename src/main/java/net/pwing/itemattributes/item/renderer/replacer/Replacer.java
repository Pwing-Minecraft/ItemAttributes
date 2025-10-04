package net.pwing.itemattributes.item.renderer.replacer;

public interface Replacer<C> {

    Replacer<C> replace(String key, C value);

    C complete();
}
