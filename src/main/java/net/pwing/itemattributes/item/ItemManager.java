package net.pwing.itemattributes.item;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.item.slot.SlotManager;
import net.pwing.itemattributes.item.tier.ItemTier;
import net.pwing.itemattributes.message.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

public class ItemManager {
    private static final NamespacedKey TEMPLATE_KEY = new NamespacedKey(ItemAttributes.getInstance(), "item_template");
    private static final NamespacedKey ID_KEY = new NamespacedKey(ItemAttributes.getInstance(), "id");
    private static final NamespacedKey TIER_KEY = new NamespacedKey(ItemAttributes.getInstance(), "tier");

    private static final NamespacedKey NAME_KEY = new NamespacedKey(ItemAttributes.getInstance(), "name");
    private static final NamespacedKey DESCRIPTION_KEY = new NamespacedKey(ItemAttributes.getInstance(), "description");

    private final ItemAttributes plugin;

    private final SlotManager slotManager;

    private final Map<Player, AttributableItem> lastEditedItem = new WeakHashMap<>();

    public ItemManager(ItemAttributes plugin) {
        this.plugin = plugin;
        this.slotManager = new SlotManager(this);

        Bukkit.getPluginManager().registerEvents(new ItemListener(this), plugin);
    }

    public SlotManager getSlotManager() {
        return this.slotManager;
    }

    @Nullable
    public ItemTemplate getTemplate(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(TEMPLATE_KEY, PersistentDataType.TAG_CONTAINER)) {
            return null;
        }

        PersistentDataContainer templateContainer = container.get(TEMPLATE_KEY, PersistentDataType.TAG_CONTAINER);
        if (!templateContainer.has(ID_KEY, PersistentDataType.STRING)) {
            return null;
        }

        String id = templateContainer.get(ID_KEY, PersistentDataType.STRING);
        return this.plugin.getItemTemplateConfig().getItemTemplates().get(id);
    }

    public void bindTemplate(ItemStack itemStack, ItemTemplate template) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        PersistentDataContainer templateContainer = container.getOrDefault(TEMPLATE_KEY, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
        templateContainer.set(ID_KEY, PersistentDataType.STRING, template.getId());
        container.set(TEMPLATE_KEY, PersistentDataType.TAG_CONTAINER, templateContainer);

        if (!((boolean) template.getOption(BuiltinItemOption.RENDER_VANILLA_ATTRIBUTES))) {
            ItemUtils.hideVisualAttributes(itemStack.getType(), meta);
        }

        itemStack.setItemMeta(meta);
    }

    @Nullable
    public ItemTier getTier(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(TIER_KEY, PersistentDataType.STRING)) {
            return null;
        }

        String id = container.get(TIER_KEY, PersistentDataType.STRING);
        return this.plugin.getTierConfig().getTiers().get(id);
    }

    public void bindTier(ItemStack itemStack, ItemTier tier) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(TIER_KEY, PersistentDataType.STRING, tier.getId());

        itemStack.setItemMeta(meta);
    }

    public String getName(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(NAME_KEY, PersistentDataType.STRING)) {
            return TextUtils.toLegacy(Component.translatable(itemStack.getTranslationKey()));
        }

        return container.get(NAME_KEY, PersistentDataType.STRING);
    }

    public void setName(ItemStack itemStack, String name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (name == null) {
            container.remove(NAME_KEY);
        } else {
            container.set(NAME_KEY, PersistentDataType.STRING, name);
        }

        itemStack.setItemMeta(meta);
    }

    public String getDescription(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(DESCRIPTION_KEY, PersistentDataType.STRING)) {
            return null;
        }

        return container.get(DESCRIPTION_KEY, PersistentDataType.STRING);
    }

    public void setDescription(ItemStack itemStack, String description) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (description == null) {
            container.remove(DESCRIPTION_KEY);
        } else {
            container.set(DESCRIPTION_KEY, PersistentDataType.STRING, description);
        }

        itemStack.setItemMeta(meta);
    }

    public AttributableItem getAttributableItem(ItemStack itemStack) {
        return this.getAttributableItem(itemStack, true);
    }

    @Nullable
    AttributableItem getAttributableItem(ItemStack itemStack, boolean snapshot) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return null;
        }

        // Check here early before we clone the ItemStack to check for the existence of a template
        if (!itemStack.getItemMeta().getPersistentDataContainer().has(TEMPLATE_KEY, PersistentDataType.TAG_CONTAINER)) {
            return null;
        }

        ItemStack ref = snapshot ? itemStack.clone() : itemStack;
        ItemTemplate template = this.getTemplate(ref);
        if (template == null) {
            return null;
        }

        return new AttributableItem(this, ref, template);
    }

    public void renderItem(ItemStack itemStack, @Nullable Player viewer) {
        AttributableItem attributableItem = this.getAttributableItem(itemStack, false);
        if (attributableItem != null) {
            attributableItem.render(viewer);
        }
    }

    @Nullable
    public AttributableItem getLastEditedItem(Player player) {
        return this.lastEditedItem.get(player);
    }

    public void setLastEditedItem(Player player, @Nullable AttributableItem item) {
        if (item == null) {
            this.lastEditedItem.remove(player);
        } else {
            this.lastEditedItem.put(player, item);
        }
    }

    public ItemAttributes getPlugin() {
        return this.plugin;
    }
}
