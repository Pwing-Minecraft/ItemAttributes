package net.pwing.itemattributes.command.tab;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.command.TabCompleter;

import java.util.Collection;

public class AttributeKeysCompleter implements TabCompleter {
    public static final AttributeKeysCompleter INSTANCE = new AttributeKeysCompleter();

    private AttributeKeysCompleter() {
    }

    @Override
    public Collection<String> tabCompletions(String previousArg, String arg, Class<?> parameter) {
        return ItemAttributes.getInstance().getAttributesConfig().getAttributes().keySet();
    }
}
