package net.pwing.itemattributes.item;

import me.redned.config.ConfigOption;
import me.redned.config.Id;
import net.pwing.itemattributes.item.config.AttributableComponentContextProvider;
import net.pwing.itemattributes.item.config.LoreCreatorContextProvider;
import net.pwing.itemattributes.item.lore.LoreCreator;
import net.pwing.itemattributes.util.RenderableComponent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ItemTemplate {
    @Id
    private String id;

    @ConfigOption(name = "name", description = "The name of the item.", required = true, contextProvider = AttributableComponentContextProvider.class)
    private RenderableComponent<AttributableItem> name;

    @ConfigOption(name = "lore", description = "The lore of the item.", required = true, contextProvider = LoreCreatorContextProvider.class)
    private List<LoreCreator> lore;

    @ConfigOption(name = "flags", description = "The flags to apply to the item.")
    private List<ItemFlag> flags;

    @ConfigOption(name = "options", description = "Options to modify how the item with this template should behave.")
    private Map<String, Object> options;

    public String getId() {
        return this.id;
    }

    public RenderableComponent<AttributableItem> getName() {
        return this.name;
    }

    public List<LoreCreator> getLore() {
        return List.copyOf(this.lore);
    }

    public List<ItemFlag> getFlags() {
        return this.flags == null ? List.of() : List.copyOf(this.flags);
    }

    @Nullable
    public <T> T getOption(String option, Class<T> optionType) {
        return optionType.cast(this.options.get(option));
    }

    @SuppressWarnings("unchecked")
    public <T> T getOption(BuiltinItemOption option) {
        if (this.options == null) {
            return (T) option.getDefaultValue();
        }

        return (T) this.options.getOrDefault(option.getName(), option.getDefaultValue());
    }
}
