package net.pwing.itemattributes;

import me.redned.config.ConfigOption;
import net.pwing.itemattributes.item.slot.SlotMechanism;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class PluginConfig {

    @ConfigOption(name = "slot-mechanism", description = "The mechanism for applying custom slots to items.", required = true)
    private SlotMechanism slotMechanism;

    @ConfigOption(name = "copy-slot-name", description = "Whether the display name of the slot creator should be used as the slot name.", required = true)
    private Map<String, Boolean> copySlotName;

    @ConfigOption(name = "bypasses-offhand-check", description = "List of items that should bypass the offhand check.", required = true)
    private List<Material> bypassesOffhandCheck;

    public SlotMechanism getSlotMechanism() {
        return this.slotMechanism;
    }

    public Map<String, Boolean> getCopySlotName() {
        return Map.copyOf(this.copySlotName);
    }

    public List<Material> getBypassesOffhandCheck() {
        return List.copyOf(this.bypassesOffhandCheck);
    }
}
