package net.pwing.itemattributes.item.tier;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.HashMap;
import java.util.Map;

public final class BuiltinTiers {
    private static final Map<String, ItemTier> VALUES = new HashMap<>();

    public static final ItemTier COMMON = register(ItemTier.builder()
            .id("common")
            .name("Common")
            .description("Common items are the most basic and easy to find items.")
            .color(NamedTextColor.WHITE)
            .build()
    );

    public static final ItemTier UNCOMMON = register(ItemTier.builder()
            .id("uncommon")
            .name("Uncommon")
            .description("Uncommon items are found less commonly but still relatively easy to find.")
            .color(NamedTextColor.DARK_GRAY)
            .build()
    );

    public static final ItemTier RARE = register(ItemTier.builder()
            .id("rare")
            .name("Rare")
            .description("Rare items are hard to find and often are more powerful.")
            .color(NamedTextColor.AQUA)
            .build()
    );

    public static final ItemTier EPIC = register(ItemTier.builder()
            .id("epic")
            .name("Epic")
            .description("Epic items don't come easily and are a real challenge to find!")
            .color(NamedTextColor.DARK_RED)
            .build()
    );

    public static final ItemTier LEGENDARY = register(ItemTier.builder()
            .id("legendary")
            .name("Legendary")
            .description("Some of the most powerful items in the game, legendary items are extremely rare.")
            .color(NamedTextColor.GOLD)
            .build()
    );

    public static final ItemTier MYTHIC = register(ItemTier.builder()
            .id("mythic")
            .name("Mythic")
            .description("The highest tier - mythic items are the rarest and most powerful items in the game.")
            .color(NamedTextColor.DARK_PURPLE)
            .build()
    );

    private static ItemTier register(ItemTier tier) {
        VALUES.put(tier.getId(), tier);
        return tier;
    }

    public static Map<String, ItemTier> get() {
        return Map.copyOf(VALUES);
    }
}
