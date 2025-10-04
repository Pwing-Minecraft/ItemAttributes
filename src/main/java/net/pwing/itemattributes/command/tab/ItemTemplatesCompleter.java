package net.pwing.itemattributes.command.tab;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.command.TabCompleter;

import java.util.Collection;

public class ItemTemplatesCompleter implements TabCompleter {
    public static final ItemTemplatesCompleter INSTANCE = new ItemTemplatesCompleter();

    private ItemTemplatesCompleter() {
    }

    @Override
    public Collection<String> tabCompletions(String previousArg, String arg, Class<?> parameter) {
        return ItemAttributes.getInstance().getItemTemplateConfig().getItemTemplates().keySet();
    }
}
