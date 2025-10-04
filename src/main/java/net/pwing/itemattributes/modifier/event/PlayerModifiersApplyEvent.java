package net.pwing.itemattributes.modifier.event;

import net.pwing.itemattributes.modifier.ItemModifier;
import net.pwing.itemattributes.modifier.ModifierType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerModifiersApplyEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ModifierType<?> modifierType;
    private final List<ItemModifier> modifiers = new ArrayList<>();

    public PlayerModifiersApplyEvent(@NotNull Player who, ModifierType<?> modifierType) {
        super(who);

        this.modifierType = modifierType;
    }

    public ModifierType<?> getModifierType() {
        return this.modifierType;
    }

    public void addModifier(ItemModifier modifier) {
        if (modifier.getType() != this.modifierType) {
            throw new IllegalArgumentException("Modifier type does not match the event type!");
        }

        this.modifiers.add(modifier);
    }

    public List<ItemModifier> getModifiers() {
        return List.copyOf(this.modifiers);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
