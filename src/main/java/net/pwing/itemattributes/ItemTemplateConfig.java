package net.pwing.itemattributes;

import com.google.common.collect.ImmutableMap;
import me.redned.config.ConfigOption;
import net.pwing.itemattributes.item.ItemTemplate;

import java.util.Map;

public class ItemTemplateConfig {

    @ConfigOption(name = "items", description = "All the item templates available.", required = true, ordered = true)
    private Map<String, ItemTemplate> items;

    public Map<String, ItemTemplate> getItemTemplates() {
        return ImmutableMap.copyOf(this.items);
    }
}
