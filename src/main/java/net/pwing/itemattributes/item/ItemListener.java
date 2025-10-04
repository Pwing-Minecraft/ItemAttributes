package net.pwing.itemattributes.item;

import net.pwing.itemattributes.attribute.event.ItemAttributeUpdateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.view.AnvilView;

public class ItemListener implements Listener {
    private final ItemManager manager;

    public ItemListener(ItemManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.manager.setLastEditedItem(event.getPlayer(), null);
    }

    @EventHandler
    public void onItemAttributeUpdate(ItemAttributeUpdateEvent event) {
        this.manager.renderItem(event.getItemStack(), null);
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        this.manager.renderItem(event.getItem(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilView view = event.getView();
        if (view.getRenameText() == null) {
            return;
        }

        if (event.getResult() == null) {
            return;
        }

        Player player = (Player) event.getView().getPlayer();
        AttributableItem attributableItem = this.manager.getAttributableItem(event.getResult());
        if (attributableItem == null) {
            return;
        }

        if (attributableItem.getTemplate().getOption(BuiltinItemOption.ALLOW_RENAMING)) {
            attributableItem.setName(event.getView().getRenameText());
            attributableItem.render(player);

            event.setResult(attributableItem.getItemStack());
        } else {
            event.setResult(null);
        }
    }
}
