package net.pwing.itemattributes.item.config;

import me.redned.config.ConfigNode;
import me.redned.config.ConfigOption;
import me.redned.config.ParseException;
import me.redned.config.context.ContextProvider;
import me.redned.config.spigot.SpigotConfigParser;
import net.pwing.itemattributes.item.lore.LoreCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoreCreatorContextProvider implements ContextProvider<List<LoreCreator>> {

    @Override
    public List<LoreCreator> provideInstance(@Nullable Path sourceFile, ConfigOption option, Class<?> type, ConfigNode node, String name, @Nullable Object scope) throws ParseException {
        if (!List.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from List when loading lore lines!")
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Node", node.getString())
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(LoreCreatorContextProvider.class)
                    .sourceFile(sourceFile);
        }

        List<LoreCreator> creators = new ArrayList<>();

        List<?> list = node.getNode(name).get(List.class);
        for (Object object : list) {
            if (!(object instanceof Map<?, ?> map)) {
                throw new ParseException("Expected " + name + " to be a list of maps when loading lore lines!")
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Node", node.getString())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(LoreCreatorContextProvider.class)
                        .sourceFile(sourceFile);
            }

            if (map.size() != 1) {
                throw new ParseException("Expected " + name + " to be a list of maps with a single key when loading lore lines!")
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Node", node.getString())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(LoreCreatorContextProvider.class)
                        .sourceFile(sourceFile);
            }

            Map.Entry<?, ?> iterator = map.entrySet().iterator().next();
            String key = iterator.getKey().toString();
            Class<? extends LoreCreator> loreCreatorType = LoreCreator.LORE_CREATORS.get(key);
            if (loreCreatorType == null) {
                throw new ParseException("Failed to find lore creator for " + key + " in configuration section " + key)
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Node", node.toString())
                        .cause(ParseException.Cause.MISSING_SECTION)
                        .type(LoreCreatorContextProvider.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            Object value = iterator.getValue();
            if (!(value instanceof Map<?, ?>)) {
                throw new ParseException("Expected " + name + " to be a list of maps with a single key and a map value when loading lore lines!")
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Node", node.toString())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(LoreCreatorContextProvider.class)
                        .sourceFile(sourceFile);
            }

            ConfigurationSection section = toMemorySection((Map<String, Object>) value);
            LoreCreator loreCreator = SpigotConfigParser.get().newInstance(sourceFile, loreCreatorType, section);

            creators.add(loreCreator);
        }

        return creators;
    }

    private static ConfigurationSection toMemorySection(Map<String, Object> map) {
        MemoryConfiguration memoryConfig = new MemoryConfiguration();
        memoryConfig.addDefaults(map);
        return memoryConfig;
    }
}
