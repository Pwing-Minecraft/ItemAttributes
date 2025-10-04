package net.pwing.itemattributes;

import me.redned.config.ConfigOption;
import me.redned.config.PostProcessable;
import net.pwing.itemattributes.attribute.ItemAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAttributesConfig implements PostProcessable {

    @ConfigOption(name = "attributes", description = "All the item attributes available.", required = true)
    private Map<String, ItemAttribute> attributes;

    private final Map<String, List<ItemAttribute>> attributesByEvent = new HashMap<>();

    public Map<String, ItemAttribute> getAttributes() {
        return Map.copyOf(this.attributes);
    }

    public List<ItemAttribute> getAttributesByEvent(String event) {
        List<ItemAttribute> attributes = this.attributesByEvent.get(event);
        return attributes == null ? List.of() : List.copyOf(attributes);
    }

    @Override
    public void postProcess() {
        for (Map.Entry<String, ItemAttribute> entry : this.attributes.entrySet()) {
            ItemAttribute attribute = entry.getValue();
            if (attribute.getAttribute().getEvent() != null) {
                this.attributesByEvent.computeIfAbsent(attribute.getAttribute().getEvent(), k -> new ArrayList<>()).add(attribute);
            }
        }
    }

    public static ItemAttributesConfig create(Map<String, ItemAttribute> attributes) {
        ItemAttributesConfig config = new ItemAttributesConfig();
        config.attributes = attributes;
        return config;
    }
}
