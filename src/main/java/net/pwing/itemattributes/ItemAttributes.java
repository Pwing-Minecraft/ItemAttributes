package net.pwing.itemattributes;

import me.redned.config.Config;
import me.redned.config.ParseException;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.pwing.itemattributes.armor.ArmorChangeListener;
import net.pwing.itemattributes.attribute.AttributeManager;
import net.pwing.itemattributes.attribute.AttributesCommand;
import net.pwing.itemattributes.attribute.BuiltinAttributes;
import net.pwing.itemattributes.command.CommandRegistry;
import net.pwing.itemattributes.feature.Features;
import net.pwing.itemattributes.item.ItemManager;
import net.pwing.itemattributes.item.ItemsCommand;
import net.pwing.itemattributes.item.tier.BuiltinTiers;
import net.pwing.itemattributes.message.MessageLoader;
import net.pwing.itemattributes.util.ColorUtils;
import net.pwing.itemattributes.util.ConfigUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class ItemAttributes extends JavaPlugin {
    private static ItemAttributes instance;
    private static BukkitAudiences audiences;

    private final ItemAttributesConfig attributesConfig = ItemAttributesConfig.create(BuiltinAttributes.get());
    private final ItemTierConfig tierConfig = ItemTierConfig.create(BuiltinTiers.get());

    private PluginConfig pluginConfig;
    private ItemTemplateConfig itemTemplateConfig;

    private AttributeManager attributeManager;
    private ItemManager itemManager;

    @Override
    public void onLoad() {
        instance = this;

        ConfigUtils.init();
    }

    @Override
    public void onEnable() {
        audiences = BukkitAudiences.create(this);

        this.loadConfigs();

        this.attributeManager = new AttributeManager(this);
        this.itemManager = new ItemManager(this);

        CommandRegistry.get().registerExecutor("attributes", "Main attributes command for ItemAttributes.", new AttributesCommand(this.attributeManager));
        CommandRegistry.get().registerExecutor("items", "Main items command for ItemAttributes.", new ItemsCommand(this.itemManager));

        // Misc things
        this.getServer().getPluginManager().registerEvents(new ArmorChangeListener(), this);

        // Enable third party features and integrations
        Features.init();
        ColorUtils.init();

        new Metrics(this, 27466);
    }

    @Override
    public void onDisable() {
    }

    public final void reload() {
        this.onReload();

        // Re-render all the items for all the online players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : onlinePlayer.getInventory()) {
                if (item == null || item.getType().isAir()) {
                    continue;
                }

                this.itemManager.renderItem(item, onlinePlayer);
            }
        }
    }

    private void onReload() {
        this.loadConfigs();
    }

    private void loadConfigs() {
        this.saveDefaultConfig();
        this.saveResource("item-templates.yml", false);

        Path dataPath = this.getDataFolder().toPath();

        try {
            this.pluginConfig = Config.parser().loadConfig(dataPath.resolve("config.yml"), PluginConfig.class);
            this.itemTemplateConfig = Config.parser().loadConfig(dataPath.resolve("item-templates.yml"), ItemTemplateConfig.class);
        } catch (ParseException e) {
            ParseException.handle(e);

            throw new RuntimeException("An error occurred loading the configs.");
        }

        MessageLoader.load(this);
    }

    @Contract(pure = false)
    public PluginConfig getPluginConfig() {
        return this.pluginConfig;
    }

    @Contract(pure = false)
    public ItemAttributesConfig getAttributesConfig() {
        return this.attributesConfig;
    }

    @Contract(pure = false)
    public ItemTemplateConfig getItemTemplateConfig() {
        return this.itemTemplateConfig;
    }

    @Contract(pure = false)
    public ItemTierConfig getTierConfig() {
        return this.tierConfig;
    }

    public AttributeManager getAttributeManager() {
        return this.attributeManager;
    }

    public ItemManager getItemManager() {
        return this.itemManager;
    }

    public Executor getMainThreadExecutor() {
        return command -> this.getServer().getScheduler().runTask(this, command);
    }

    public static ItemAttributes getInstance() {
        return instance;
    }

    public static BukkitAudiences getAudiences() {
        return audiences;
    }
}
