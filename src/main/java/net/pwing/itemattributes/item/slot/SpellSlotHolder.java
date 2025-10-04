package net.pwing.itemattributes.item.slot;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.feature.spell.SpellHolder;
import net.pwing.itemattributes.feature.spell.Spells;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.message.Messages;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class SpellSlotHolder extends SlotHolder {
    private static final NamespacedKey SPELL_KEY = new NamespacedKey(ItemAttributes.getInstance(), "spell");

    private SpellHolder spell;

    public SpellSlotHolder(PersistentDataContainer container) {
        super(SlotType.SPELL, container);

        String spellKey = container.get(SPELL_KEY, PersistentDataType.STRING);
        if (spellKey == null) {
            return;
        }

        this.spell = Spells.getSpell(NamespacedKey.fromString(spellKey));
    }

    public SpellSlotHolder(PersistentDataContainer container, SpellHolder spell) {
        super(SlotType.SPELL, container);

        this.spell = spell;
    }

    @Override
    public void save() {
        super.save();

        if (this.spell == null) {
            return;
        }

        this.getDataContainer().set(SPELL_KEY, PersistentDataType.STRING, this.spell.getKey().toString());
    }

    @Override
    public boolean isEmpty() {
        return this.spell == null;
    }

    @Override
    protected Component renderSlotInfo(AttributableItem item) {
        if (this.spell == null) {
            return Component.empty();
        }

        return item.render(this.spell.getDisplayName());
    }

    @Override
    public List<Component> describeSlotInfo() {
        return List.of(Component.text("Spell: ", Messages.PRIMARY_COLOR)
                .append(this.spell.getDisplayName())
                .append(Component.space())
                .append(Component.text("(" + this.spell.getKey().getNamespace() + ")", Messages.SECONDARY_COLOR)));
    }

    @Override
    public void applyFromRawArgs(String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Spell name cannot be empty");
        }

        NamespacedKey spellKey = NamespacedKey.fromString(args[0]);
        if (spellKey == null) {
            throw new IllegalArgumentException("Invalid spell key format");
        }

        SpellHolder spell = Spells.getSpell(spellKey);
        if (spell == null) {
            throw new IllegalArgumentException("Could not find a spell with the name: " + spellKey);
        }

        this.spell = spell;
    }

    @Override
    public void applyFromHolder(SlotHolder holder) {
        if (!(holder instanceof SpellSlotHolder toApply)) {
            throw new IllegalArgumentException("Cannot apply from a non-spell slot holder!");
        }

        super.applyFromHolder(holder);

        if (toApply.spell == null) {
            return;
        }

        this.spell = toApply.spell;
    }

    public SpellHolder getSpell() {
        return this.spell;
    }
}
