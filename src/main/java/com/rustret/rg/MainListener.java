package com.rustret.rg;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import com.rustret.rg.objects.Selection;

import java.util.UUID;

public class MainListener implements Listener {
    private final Storage storage;
    private final Messages messages;
    public MainListener(Storage storage, Messages messages) {
        this.storage = storage;
        this.messages = messages;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.isCancelled() || event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Item item = event.getItem();

        if (item.getId() != Item.WOODEN_AXE || !item.hasEnchantment(-1)) {
            return;
        }

        Position pos = event.getBlock().floor();
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        storage.selections.putIfAbsent(id, new Selection());
        Selection s = storage.selections.get(id);

        if (s.pos1 == null) {
            s.pos1 = pos;
            player.sendMessage(String.format(messages.SELECTION_POS1, (int)pos.x, (int)pos.y, (int)pos.z));
        }
        else if (s.pos2 == null) {
            s.pos2 = pos;
            player.sendMessage(String.format(messages.SELECTION_POS2, (int)pos.x, (int)pos.y, (int)pos.z));
        }
        else {
            s.pos1 = pos;
            s.pos2 = null;
            player.sendMessage(String.format(messages.SELECTION_POS1, (int)pos.x, (int)pos.y, (int)pos.z));
        }
    }
}
