package net.pwing.itemattributes.attribute;

import com.google.common.base.Preconditions;
import me.redned.config.ConfigOption;
import me.redned.config.Id;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.requirement.ItemRequirement;
import net.pwing.itemattributes.requirement.RequirementType;
import net.pwing.itemattributes.requirement.config.AnyItemRequirementContextProvider;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.Set;

public class ItemAttribute {
    @Id
    private String id;

    @ConfigOption(name = "name", description = "The name of the attribute.", required = true)
    private String name;

    @ConfigOption(name = "type", description = "The type of the attribute.", required = true)
    private AttributeType type;

    @ConfigOption(name = "description", description = "The description of the attribute.", required = true)
    private String description;

    @ConfigOption(name = "display", description = "The display of the attribute.", required = true)
    private Component display;

    @ConfigOption(name = "color", description = "The color of the attribute.", required = true)
    private TextColor color;

    @ConfigOption(name = "slots", description = "The slots the attribute can be applied to.", required = true)
    private List<SlotGroup> slots;

    @ConfigOption(name = "attribute", description = "The attribute applicator to apply.", required = true)
    private AttributeApplicator attribute;

    @ConfigOption(name = "requirements", description = "The requirements to apply the attribute.", contextProvider = AnyItemRequirementContextProvider.class)
    private List<ItemRequirement<?>> requirements;

    public String getId() {
        return this.id;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey(ItemAttributes.getInstance(), this.id);
    }

    public String getName() {
        return this.name;
    }

    public AttributeType getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public TextColor getColor() {
        return this.color;
    }

    public Component getDisplay() {
        return this.display;
    }

    public Set<SlotGroup> getSlots() {
        return Set.copyOf(this.slots);
    }

    public AttributeApplicator getAttribute() {
        return this.attribute;
    }

    public List<ItemRequirement<?>> getAllRequirements() {
        return this.requirements == null ? List.of() : List.copyOf(this.requirements);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> List<ItemRequirement<T>> getRequirements(RequirementType<T> type) {
        return (List) this.requirements.stream().filter(requirement -> requirement.getRequirementType() == type || requirement.getRequirementType() == RequirementType.UNIVERSAL).toList();
    }

    static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private AttributeType type;
        private String description;
        private Component display;
        private TextColor color;
        private List<SlotGroup> slots;
        private AttributeApplicator.Builder attribute;
        private List<ItemRequirement<?>> requirements;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(AttributeType type) {
            this.type = type;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder display(Component display) {
            this.display = display;
            return this;
        }

        public Builder color(TextColor color) {
            this.color = color;
            return this;
        }

        public Builder slots(List<SlotGroup> slots) {
            this.slots = slots;
            return this;
        }

        public Builder attribute(AttributeApplicator.Builder attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder requirements(List<ItemRequirement<?>> requirements) {
            this.requirements = requirements;
            return this;
        }

        public ItemAttribute build() {
            Preconditions.checkNotNull(this.id, "The id of the attribute cannot be null.");
            Preconditions.checkNotNull(this.name, "The name of the attribute cannot be null.");
            Preconditions.checkNotNull(this.type, "The type of the attribute cannot be null.");
            Preconditions.checkNotNull(this.description, "The description of the attribute cannot be null.");
            Preconditions.checkNotNull(this.display, "The display of the attribute cannot be null.");
            Preconditions.checkNotNull(this.color, "The color of the attribute cannot be null.");
            Preconditions.checkNotNull(this.slots, "The slots of the attribute cannot be null.");
            Preconditions.checkNotNull(this.attribute, "The attribute cannot be null.");

            ItemAttribute itemAttribute = new ItemAttribute();
            itemAttribute.id = this.id;
            itemAttribute.name = this.name;
            itemAttribute.type = this.type;
            itemAttribute.description = this.description;
            itemAttribute.display = this.display;
            itemAttribute.color = this.color;
            itemAttribute.slots = this.slots;
            itemAttribute.attribute = this.attribute.attribute(itemAttribute).build();
            itemAttribute.requirements = this.requirements == null ? List.of() : this.requirements;
            return itemAttribute;
        }
    }
}
