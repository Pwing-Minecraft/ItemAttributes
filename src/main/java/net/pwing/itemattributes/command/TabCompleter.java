package net.pwing.itemattributes.command;

import net.pwing.itemattributes.command.tab.AttributeKeysCompleter;
import net.pwing.itemattributes.command.tab.ItemTemplatesCompleter;
import net.pwing.itemattributes.command.tab.ItemTiersCompleter;

import java.util.Collection;
import java.util.Map;

public interface TabCompleter {
    Map<Class<? extends TabCompleter>, TabCompleter> TAB_COMPLETERS = Map.of(
            AttributeKeysCompleter.class, AttributeKeysCompleter.INSTANCE,
            ItemTemplatesCompleter.class, ItemTemplatesCompleter.INSTANCE,
            ItemTiersCompleter.class, ItemTiersCompleter.INSTANCE
    );

    Collection<String> tabCompletions(String previousArg, String arg, Class<?> parameter);
}
