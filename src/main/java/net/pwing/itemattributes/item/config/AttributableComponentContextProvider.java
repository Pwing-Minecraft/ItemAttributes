package net.pwing.itemattributes.item.config;

import me.redned.config.ConfigNode;
import me.redned.config.ConfigOption;
import me.redned.config.ParseException;
import me.redned.config.context.ContextProvider;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.util.RenderableComponent;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class AttributableComponentContextProvider implements ContextProvider<RenderableComponent<AttributableItem>> {

    @Override
    public RenderableComponent<AttributableItem> provideInstance(@Nullable Path sourceFile, ConfigOption option, Class<?> type, ConfigNode node, String name, @Nullable Object scope) throws ParseException {
        return new RenderableComponent<>(node.getNode(name).getString(), AttributableItem::renderFromTemplate);
    }
}
