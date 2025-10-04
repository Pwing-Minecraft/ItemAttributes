package net.pwing.itemattributes.requirement.config;

import me.redned.config.ConfigNode;
import me.redned.config.ConfigOption;
import me.redned.config.ParseException;
import me.redned.config.context.ContextProvider;
import me.redned.config.spigot.SpigotConfigParser;
import net.pwing.itemattributes.requirement.ItemRequirement;
import net.pwing.itemattributes.requirement.ItemRequirementType;
import net.pwing.itemattributes.requirement.RequirementType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ItemRequirementContextProvider<T> implements ContextProvider<List<ItemRequirement<T>>> {
    private final RequirementType<T> type;

    public ItemRequirementContextProvider(RequirementType<T> type) {
        this.type = type;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public List<ItemRequirement<T>> provideInstance(@Nullable Path sourceFile, ConfigOption option, Class<?> type, ConfigNode node, String name, @Nullable Object scope) throws ParseException {
        if (!List.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from List when loading item requirements!")
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Node", node.getString())
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(ItemRequirementContextProvider.class)
                    .sourceFile(sourceFile);
        }

        List<ItemRequirement<T>> requirements = new ArrayList<>();
        List<?> list = node.getNode(name).get(List.class);
        if (list == null) {
            return requirements;
        }

        for (Object object : list) {
            if (!(object instanceof Map<?, ?> map)) {
                throw new ParseException("Expected " + name + " to be a list of maps when loading item requirements!")
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Node", node.getString())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(ItemRequirementContextProvider.class)
                        .sourceFile(sourceFile);
            }

            if (map.size() != 1) {
                throw new ParseException("Expected " + name + " to be a list of maps with a single key when loading item requirements!")
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Node", node.getString())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(ItemRequirementContextProvider.class)
                        .sourceFile(sourceFile);
            }

            Map.Entry<?, ?> iterator = map.entrySet().iterator().next();
            String key = iterator.getKey().toString();

            ItemRequirementType<T, ItemRequirement<T>> itemRequirementType = this.getItemRequirementType(key);
            if (itemRequirementType == null) {
                throw new ParseException("Failed to find item requirement for " + key + " in configuration section " + key)
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Node", node.getString())
                        .cause(ParseException.Cause.INVALID_VALUE)
                        .type(ItemRequirementContextProvider.class)
                        .sourceFile(sourceFile);
            }
            
            Object value = iterator.getValue();
            if (!(value instanceof Map<?,?>)) {
                throw new ParseException("Expected " + name + " to be a list of maps with a single key when loading item requirements!")
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Node", node.getString())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(ItemRequirementContextProvider.class)
                        .sourceFile(sourceFile);
            }

            ConfigurationSection section = toMemorySection((Map<String, Object>) value);
            ItemRequirement<T> itemRequirement = SpigotConfigParser.get().newInstance(sourceFile, itemRequirementType.getRequirementType(), section);

            requirements.add(itemRequirement);
        }

        return requirements;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ItemRequirementType<T, ItemRequirement<T>> getItemRequirementType(String key) {
        ItemRequirementType<T, ItemRequirement<T>> itemRequirementType = ItemRequirementType.get(this.type, key);
        if (itemRequirementType == null) {
            itemRequirementType = (ItemRequirementType) ItemRequirementType.get(RequirementType.UNIVERSAL, key);
        }

        return itemRequirementType;
    }

    private static ConfigurationSection toMemorySection(Map<String, Object> map) {
        MemoryConfiguration memoryConfig = new MemoryConfiguration();
        memoryConfig.addDefaults(map);
        return memoryConfig;
    }
}
