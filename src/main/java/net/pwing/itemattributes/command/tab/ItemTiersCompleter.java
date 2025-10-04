package net.pwing.itemattributes.command.tab;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.command.TabCompleter;

import java.util.Collection;

public class ItemTiersCompleter implements TabCompleter {
    public static final ItemTiersCompleter INSTANCE = new ItemTiersCompleter();

    private ItemTiersCompleter() {
    }

    @Override
    public Collection<String> tabCompletions(String previousArg, String arg, Class<?> parameter) {
        return ItemAttributes.getInstance().getTierConfig().getTiers().keySet();
    }
}
