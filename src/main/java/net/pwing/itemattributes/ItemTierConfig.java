package net.pwing.itemattributes;

import com.google.common.collect.ImmutableMap;
import me.redned.config.ConfigOption;
import net.pwing.itemattributes.item.tier.ItemTier;

import java.util.Map;

public class ItemTierConfig {

    @ConfigOption(name = "tiers", description = "All the item tiers available.", required = true, ordered = true)
    private Map<String, ItemTier> tiers;

    public Map<String, ItemTier> getTiers() {
        return ImmutableMap.copyOf(this.tiers);
    }

    public static ItemTierConfig create(Map<String, ItemTier> tiers) {
        ItemTierConfig config = new ItemTierConfig();
        config.tiers = tiers;
        return config;
    }
}
