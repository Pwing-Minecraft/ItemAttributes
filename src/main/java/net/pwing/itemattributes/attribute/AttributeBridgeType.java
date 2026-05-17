package net.pwing.itemattributes.attribute;

import net.pwing.itemattributes.attribute.bridge.AttributeBridge;
import net.pwing.itemattributes.attribute.bridge.BuiltinAttributeBridge;
import net.pwing.itemattributes.attribute.bridge.ExternalAttributeBridge;
import net.pwing.itemattributes.attribute.bridge.RedirectAttributeBridge;
import net.pwing.itemattributes.attribute.bridge.VanillaAttributeBridge;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public enum AttributeBridgeType {
    VANILLA(VanillaAttributeBridge.INSTANCE),
    EXTERNAL(ExternalAttributeBridge.INSTANCE),
    BUILTIN(BuiltinAttributeBridge.INSTANCE),
    REDIRECT(RedirectAttributeBridge.INSTANCE);

    private final AttributeBridge bridge;

    AttributeBridgeType(AttributeBridge bridge) {
        this.bridge = bridge;
    }

    AttributeBridge getBridge() {
        return this.bridge;
    }

    public Number getValue(Player player, NamespacedKey key) {
        return this.bridge.getValue(player, key);
    }

    public void apply(Player player, AttributeApplicator applicator, Number value) {
        this.bridge.apply(player, applicator, value);
    }

    public void reset(Player player, AttributeApplicator applicator) {
        this.bridge.reset(player, applicator);
    }
}
