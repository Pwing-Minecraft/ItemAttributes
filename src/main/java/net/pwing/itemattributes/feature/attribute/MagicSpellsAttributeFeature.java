package net.pwing.itemattributes.feature.attribute;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaBar;
import com.nisovin.magicspells.mana.ManaRank;
import com.nisovin.magicspells.mana.ManaSystem;
import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.attribute.AttributeCalculator;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

public class MagicSpellsAttributeFeature extends PluginFeature<AttributesFeature> implements AttributesFeature {
    private final Method getManaBarMethod;

    public MagicSpellsAttributeFeature() {
        super("MagicSpells");

        try {
            this.getManaBarMethod = ManaSystem.class.getDeclaredMethod("getManaBar", Player.class);
            this.getManaBarMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to access getManaBar method", e);
        }
    }

    @Override
    public Number getValue(Player player, NamespacedKey attributeKey) {
        MagicSpellsAttribute attribute = getAttribute(attributeKey);
        if (attribute == null) {
            return null;
        }

        return attribute.getValue(player);
    }

    @Override
    public void apply(Player player, AttributeApplicator applicator, Number value) {
        ManaRank rank = this.getManaRank(player);
        int baseMaxMana = rank.getMaxMana();

        MagicSpellsAttribute attribute = getAttribute(applicator.getKey());
        if (attribute == null) {
            return;
        }

        attribute.apply(player, (int) AttributeCalculator.calculateAttributeValue(baseMaxMana, value.doubleValue(), applicator.getOperation()));

        // Refresh the mana when we update it
        MagicSpells.getManaHandler().showMana(player);
    }

    @Override
    public void reset(Player player, NamespacedKey attributeKey) {
        ManaRank rank = this.getManaRank(player);
        int baseMaxMana = rank.getMaxMana();

        MagicSpellsAttribute attribute = getAttribute(attributeKey);
        if (attribute == null) {
            return;
        }

        attribute.apply(player, baseMaxMana);

        // Refresh the mana when we update it
        MagicSpells.getManaHandler().showMana(player);
    }

    private ManaRank getManaRank(Player player) {
        if (!(MagicSpells.getManaHandler() instanceof ManaSystem system)) {
            return null;
        }

        try {
            ManaBar bar = (ManaBar) this.getManaBarMethod.invoke(system, player);
            return bar.getManaRank();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private static MagicSpellsAttribute getAttribute(NamespacedKey key) {
        try {
            return MagicSpellsAttribute.valueOf(key.getKey().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public enum MagicSpellsAttribute {
        MANA_CAPACITY("mana_capacity", MagicSpells.getManaHandler()::getMana, MagicSpells.getManaHandler()::setMaxMana),
        MANA_REGEN("mana_regen", MagicSpells.getManaHandler()::getRegenAmount, MagicSpells.getManaHandler()::setRegenAmount);

        private final String key;
        private final ToIntFunction<Player> attributeGetter;
        private final ObjIntConsumer<Player> attributeApplier;

        MagicSpellsAttribute(String key, ToIntFunction<Player> attributeGetter, ObjIntConsumer<Player> attributeApplier) {
            this.key = key;
            this.attributeGetter = attributeGetter;
            this.attributeApplier = attributeApplier;
        }

        public String getKey() {
            return this.key;
        }

        public int getValue(Player player) {
            return this.attributeGetter.applyAsInt(player);
        }

        public void apply(Player player, int value) {
            this.attributeApplier.accept(player, value);
        }
    }
}
