package net.pwing.itemattributes.attribute;

import net.pwing.itemattributes.attribute.bridge.AttributeBridge;
import net.pwing.itemattributes.attribute.bridge.BuiltinAttributeBridge;
import net.pwing.itemattributes.attribute.bridge.ExternalAttributeBridge;
import net.pwing.itemattributes.attribute.bridge.VanillaAttributeBridge;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public enum AttributeBridgeType {
    VANILLA(VanillaAttributeBridge.INSTANCE),
    EXTERNAL(ExternalAttributeBridge.INSTANCE),
    BUILTIN(BuiltinAttributeBridge.INSTANCE);

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
}
