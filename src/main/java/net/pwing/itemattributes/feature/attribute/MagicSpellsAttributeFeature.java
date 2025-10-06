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
    private static final Method GET_MANA_BAR_METHOD;

    static {
        try {
            GET_MANA_BAR_METHOD = ManaSystem.class.getDeclaredMethod("getManaBar", Player.class);
            GET_MANA_BAR_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to access getManaBar method", e);
        }
    }

    public MagicSpellsAttributeFeature() {
        super("MagicSpells");
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
        MagicSpellsAttribute attribute = getAttribute(applicator.getKey());
        if (attribute == null) {
            return;
        }

        attribute.apply(player, (int) AttributeCalculator.calculateAttributeValue(attribute.getBaseValue(player), value.doubleValue(), applicator.getOperation()));

        // Refresh the mana when we update it
        MagicSpells.getManaHandler().showMana(player);
    }

    @Override
    public void reset(Player player, NamespacedKey attributeKey) {
        MagicSpellsAttribute attribute = getAttribute(attributeKey);
        if (attribute == null) {
            return;
        }

        attribute.apply(player, attribute.getBaseValue(player));

        // Refresh the mana when we update it
        MagicSpells.getManaHandler().showMana(player);
    }

    private static ManaRank getManaRank(Player player) {
        if (!(MagicSpells.getManaHandler() instanceof ManaSystem system)) {
            return null;
        }

        try {
            ManaBar bar = (ManaBar) GET_MANA_BAR_METHOD.invoke(system, player);
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
        MANA_CAPACITY("mana_capacity", player -> getManaRank(player).getMaxMana(), MagicSpells.getManaHandler()::getMaxMana, MagicSpells.getManaHandler()::setMaxMana),
        MANA_REGEN("mana_regen", player -> getManaRank(player).getRegenAmount(), MagicSpells.getManaHandler()::getRegenAmount, MagicSpells.getManaHandler()::setRegenAmount);

        private final String key;
        private final ToIntFunction<Player> baseAttributeGetter;
        private final ToIntFunction<Player> attributeGetter;
        private final ObjIntConsumer<Player> attributeApplier;

        MagicSpellsAttribute(String key, ToIntFunction<Player> baseAttributeGetter, ToIntFunction<Player> attributeGetter, ObjIntConsumer<Player> attributeApplier) {
            this.key = key;
            this.baseAttributeGetter = baseAttributeGetter;
            this.attributeGetter = attributeGetter;
            this.attributeApplier = attributeApplier;
        }

        public String getKey() {
            return this.key;
        }

        public int getBaseValue(Player player) {
            return this.baseAttributeGetter.applyAsInt(player);
        }

        public int getValue(Player player) {
            return this.attributeGetter.applyAsInt(player);
        }

        public void apply(Player player, int value) {
            this.attributeApplier.accept(player, value);
        }
    }
}
