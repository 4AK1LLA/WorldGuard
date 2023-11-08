//package com.rustret.rg.selection;
//
//import cn.nukkit.Player;
//import cn.nukkit.event.EventHandler;
//import cn.nukkit.event.Listener;
//import cn.nukkit.event.player.PlayerInteractEvent;
//import cn.nukkit.item.Item;
//import cn.nukkit.level.Position;
//import com.rustret.rg.Messages;
//import com.rustret.rg.selection.objects.SelectionResponse;
//
//public class SelectionListener implements Listener {
//    private final SelectionManager sManager;
//    private final Messages messages;
//
//    public SelectionListener(SelectionManager sManager, Messages messages) {
//        this.sManager = sManager;
//        this.messages = messages;
//    }
//
//    //Event might be cancelled
//    @EventHandler
//    public void onInteract(PlayerInteractEvent event) {
//        if (event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
//            return;
//        }
//
//        Item item = event.getItem();
//
//        if (item.getId() != Item.WOODEN_AXE || !item.hasEnchantment(-1)) {
//            return;
//        }
//
//        Position pos = event.getBlock().floor();
//        Player player = event.getPlayer();
//
//        SelectionResponse response = sManager.put(player.getUniqueId(), pos);
//        switch (response) {
//            case POS1: player.sendMessage(String.format(messages.SELECTION_POS1, (int)pos.x, (int)pos.y, (int)pos.z)); break;
//            case POS2: player.sendMessage(String.format(messages.SELECTION_POS2, (int)pos.x, (int)pos.y, (int)pos.z)); break;
//        }
//    }
//}
