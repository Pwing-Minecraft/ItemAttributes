package net.pwing.itemattributes.attribute;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.pwing.itemattributes.requirement.RequiresAttributeRequirement;
import net.pwing.itemattributes.util.IntRange;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BuiltinAttributes {
    private static final Map<String, ItemAttribute> VALUES = new HashMap<>();

    public static final ItemAttribute HEALTH = register(ItemAttribute.builder()
            .id("health")
            .name("Health")
            .type(AttributeType.INTEGER)
            .description("Adds health to the player.")
            .display(Component.text("%operator%%value% Health", NamedTextColor.GREEN))
            .color(NamedTextColor.GREEN)
            .slots(List.of(SlotGroup.BODY))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.VANILLA)
                    .key(NamespacedKey.minecraft("max_health"))
                    .operation(AttributeOperation.ADD)
            )
            .build()
    );

    public static final ItemAttribute DAMAGE = register(ItemAttribute.builder()
            .id("damage")
            .name("Damage")
            .type(AttributeType.INTEGER)
            .description("Adds damage to the player.")
            .display(Component.text("%operator%%value% Damage", NamedTextColor.RED))
            .color(NamedTextColor.RED)
            .slots(List.of(SlotGroup.MAIN_HAND, SlotGroup.OFF_HAND))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.VANILLA)
                    .key(NamespacedKey.minecraft("attack_damage"))
                    .operation(AttributeOperation.ADD)
            )
            .build()
    );

    public static final ItemAttribute CRITICAL_DAMAGE = register(ItemAttribute.builder()
            .id("critical_damage")
            .name("Critical Damage")
            .type(AttributeType.INTEGER)
            .description("Adds critical damage to the player.")
            .display(Component.text("%operator%%value% Critical Damage", NamedTextColor.RED))
            .color(NamedTextColor.RED)
            .slots(List.of(SlotGroup.MAIN_HAND, SlotGroup.OFF_HAND))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.BUILTIN)
                    .key(NamespacedKey.fromString("itemattributes:critical_damage"))
                    .operation(AttributeOperation.ADD)
                    .event("entity_damage")
            )
            .requirements(List.of(RequiresAttributeRequirement.builder()
                    .type(AttributeBridgeType.BUILTIN)
                    .key(NamespacedKey.fromString("itemattributes:critical_chance"))
                    .predicate(AttributePredicate.PROBABILITY)
                    .range(new IntRange(0, 100))
                    .build()
            ))
            .build()
    );

    public static final ItemAttribute CRITICAL_CHANCE = register(ItemAttribute.builder()
            .id("critical_chance")
            .name("Critical Chance")
            .type(AttributeType.INTEGER)
            .description("Adds critical chance to the player.")
            .display(Component.text("%operator%%value%% Critical Chance", NamedTextColor.GOLD))
            .color(NamedTextColor.GOLD)
            .slots(List.of(SlotGroup.MAIN_HAND, SlotGroup.OFF_HAND))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.BUILTIN)
                    .key(NamespacedKey.fromString("itemattributes:critical_chance"))
                    .operation(AttributeOperation.ADD)
            )
            .build()
    );

    public static final ItemAttribute REGEN = register(ItemAttribute.builder()
            .id("regen")
            .name("Regen")
            .type(AttributeType.INTEGER)
            .description("Adds health regen to the player.")
            .display(Component.text("%operator%%value% Regen", NamedTextColor.LIGHT_PURPLE))
            .color(NamedTextColor.LIGHT_PURPLE)
            .slots(List.of(SlotGroup.BODY))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.BUILTIN)
                    .key(NamespacedKey.fromString("itemattributes:regen"))
                    .operation(AttributeOperation.ADD)
                    .modifierExpression("%value%/3")
                    .event("regen")
            )
            .build()
    );

    public static final ItemAttribute SPEED = register(ItemAttribute.builder()
            .id("speed")
            .name("Speed")
            .type(AttributeType.INTEGER)
            .description("Adds speed to the player.")
            .display(Component.text("%operator%%value%% Speed", NamedTextColor.AQUA))
            .color(NamedTextColor.AQUA)
            .slots(List.of(SlotGroup.FEET))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.VANILLA)
                    .key(NamespacedKey.minecraft("movement_speed"))
                    .operation(AttributeOperation.TOTAL_PERCENTAGE)
            )
            .build()
    );

    public static final ItemAttribute JUMP = register(ItemAttribute.builder()
            .id("jump")
            .name("Jump")
            .type(AttributeType.INTEGER)
            .description("Adds jump boost to the player.")
            .display(Component.text("%operator%%value% Jump", NamedTextColor.DARK_GREEN))
            .color(NamedTextColor.DARK_GREEN)
            .slots(List.of(SlotGroup.FEET))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.VANILLA)
                    .key(NamespacedKey.minecraft("jump_strength"))
                    .operation(AttributeOperation.TOTAL_PERCENTAGE)
            )
            .build()
    );

    public static final ItemAttribute ARMOR = register(ItemAttribute.builder()
            .id("armor")
            .name("Armor")
            .type(AttributeType.INTEGER)
            .description("Adds additional armor to the player.")
            .display(Component.text("%operator%%value% Armor", NamedTextColor.GRAY))
            .color(NamedTextColor.GRAY)
            .slots(List.of(SlotGroup.BODY))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.VANILLA)
                    .key(NamespacedKey.minecraft("armor"))
                    .operation(AttributeOperation.ADD)
            )
            .build()
    );

    public static final ItemAttribute DEFENSE = register(ItemAttribute.builder()
            .id("defense")
            .name("Defense")
            .type(AttributeType.INTEGER)
            .description("Adds protection to the player.")
            .display(Component.text("%operator%%value% Defense", NamedTextColor.DARK_RED))
            .color(NamedTextColor.DARK_RED)
            .slots(List.of(SlotGroup.BODY))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.BUILTIN)
                    .key(NamespacedKey.fromString("itemattributes:defense"))
                    .operation(AttributeOperation.ADD)
                    .modifierExpression("-min(1.0, ceil(min(25, (6 + %value%^2) * 0.75 / 3) * (0.5 + 0.5 * rand)) * 0.04)")
                    .event("entity_take_damage")
            )
            .build()
    );

    public static final ItemAttribute MANA_CAPACITY = register(ItemAttribute.builder()
            .id("mana_capacity")
            .name("Mana Capacity")
            .type(AttributeType.INTEGER)
            .description("Adds mana to the player.")
            .display(Component.text("%operator%%value% Mana Capacity", NamedTextColor.BLUE))
            .color(NamedTextColor.BLUE)
            .slots(List.of(SlotGroup.ALL))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.EXTERNAL)
                    .key(NamespacedKey.fromString("magicspells:mana_capacity"))
                    .operation(AttributeOperation.ADD)
            )
            .build()
    );

    public static final ItemAttribute MANA_REGEN = register(ItemAttribute.builder()
            .id("mana_regen")
            .name("Mana Regen")
            .type(AttributeType.INTEGER)
            .description("Increases the mana regeneration of the player.")
            .display(Component.text("%operator%%value% Mana Regen", NamedTextColor.DARK_PURPLE))
            .color(NamedTextColor.DARK_PURPLE)
            .slots(List.of(SlotGroup.ALL))
            .attribute(AttributeApplicator.builder()
                    .type(AttributeBridgeType.EXTERNAL)
                    .key(NamespacedKey.fromString("magicspells:mana_regen"))
                    .operation(AttributeOperation.ADD)
            )
            .build()
    );

    private static ItemAttribute register(ItemAttribute attribute) {
        VALUES.put(attribute.getId(), attribute);
        return attribute;
    }

    public static Map<String, ItemAttribute> get() {
        return Map.copyOf(VALUES);
    }
}
