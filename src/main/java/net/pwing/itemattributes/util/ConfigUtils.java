package net.pwing.itemattributes.util;

import me.redned.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pwing.itemattributes.item.config.AttributableComponentContextProvider;
import net.pwing.itemattributes.item.config.LoreCreatorContextProvider;
import net.pwing.itemattributes.modifier.config.ItemModifierContextProvider;
import net.pwing.itemattributes.requirement.config.AnyItemRequirementContextProvider;
import net.pwing.itemattributes.requirement.config.AttributeItemRequirementContextProvider;
import net.pwing.itemattributes.requirement.config.EntityItemRequirementContextProvider;
import net.pwing.itemattributes.requirement.config.ItemItemRequirementContextProvider;
import org.bukkit.NamespacedKey;

public class ConfigUtils {

    public static void init() {
        Config.parser().registerContextProvider(AnyItemRequirementContextProvider.class, new AnyItemRequirementContextProvider<>());
        Config.parser().registerContextProvider(AttributableComponentContextProvider.class, new AttributableComponentContextProvider());
        Config.parser().registerContextProvider(ItemModifierContextProvider.class, new ItemModifierContextProvider());
        Config.parser().registerContextProvider(LoreCreatorContextProvider.class, new LoreCreatorContextProvider());

        Config.parser().registerContextProvider(AttributeItemRequirementContextProvider.class, new AttributeItemRequirementContextProvider());
        Config.parser().registerContextProvider(EntityItemRequirementContextProvider.class, new EntityItemRequirementContextProvider());
        Config.parser().registerContextProvider(ItemItemRequirementContextProvider.class, new ItemItemRequirementContextProvider());

        Config.parser().registerProvider(TextColor.class, configValue -> {
            if (!(configValue instanceof String value)) {
                return null;
            }

            if (!value.startsWith("#")) {
                return NamedTextColor.NAMES.value(value);
            }

            return TextColor.fromHexString(value);
        });

        Config.parser().registerProvider(Component.class, configValue -> {
            if (!(configValue instanceof String value)) {
                return null;
            }

            return MiniMessage.miniMessage().deserialize(value);
        });

        Config.parser().registerProvider(NamespacedKey.class, configValue -> {
            if (!(configValue instanceof String value)) {
                return null;
            }

            return NamespacedKey.fromString(value);
        });

        Config.parser().registerProvider(IntRange.class, configValue -> {
            // If config value is not a string or number, return null
            if (!(configValue instanceof String || configValue instanceof Number)) {
                return null;
            }

            String value = configValue.toString();
            if (value.contains("-")) {
                String[] split = value.split("-");
                return new IntRange(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            } else {
                if (value.endsWith("+")) {
                    return IntRange.minInclusive(Integer.parseInt(value.substring(0, value.length() - 1)));
                } else if (value.startsWith("+")) {
                    return IntRange.maxInclusive(Integer.parseInt(value.substring(1)));
                }

                return new IntRange(Integer.parseInt(value));
            }
        });

        Config.serializer().registerSerializer(Component.class, (node, section, type) -> {
            section.getNode(node).set(MiniMessage.miniMessage().serialize(type));
        });

        Config.serializer().registerSerializer(NamespacedKey.class, (node, section, type) -> {
            section.getNode(node).set(type.toString());
        });
    }
}
