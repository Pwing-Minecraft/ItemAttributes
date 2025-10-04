package net.pwing.itemattributes.item.tier;

import com.google.common.base.Preconditions;
import me.redned.config.ConfigOption;
import me.redned.config.Id;
import net.kyori.adventure.text.format.TextColor;

public class ItemTier {
    @Id
    private String id;

    @ConfigOption(name = "name", description = "The name of the tier.", required = true)
    private String name;

    @ConfigOption(name = "description", description = "The description of the tier.", required = true)
    private String description;

    @ConfigOption(name = "color", description = "The color of the tier.", required = true)
    private TextColor color;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public TextColor getColor() {
        return this.color;
    }

    static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private TextColor color;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder color(TextColor color) {
            this.color = color;
            return this;
        }

        public ItemTier build() {
            Preconditions.checkNotNull(this.id, "The id must not be null");
            Preconditions.checkNotNull(this.name, "The name must not be null");
            Preconditions.checkNotNull(this.description, "The description must not be null");
            Preconditions.checkNotNull(this.color, "The color must not be null");

            ItemTier tier = new ItemTier();
            tier.id = this.id;
            tier.name = this.name;
            tier.description = this.description;
            tier.color = this.color;
            return tier;
        }
    }
}
