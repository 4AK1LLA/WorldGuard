//package com.rustret.rg.listeners;
//
//import cn.nukkit.Player;
//import cn.nukkit.block.Block;
//import cn.nukkit.event.Event;
//import cn.nukkit.event.EventHandler;
//import cn.nukkit.event.EventPriority;
//import cn.nukkit.event.Listener;
//import cn.nukkit.event.block.BlockBreakEvent;
//import cn.nukkit.event.block.BlockPlaceEvent;
//import cn.nukkit.event.block.ItemFrameDropItemEvent;
//import cn.nukkit.event.entity.EntityDamageByEntityEvent;
//import cn.nukkit.event.player.PlayerBucketEmptyEvent;
//import cn.nukkit.event.player.PlayerInteractEvent;
//import com.rustret.rg.Messages;
//import com.rustret.rg.WorldGuardContext;
//
//import java.util.List;
//import java.util.UUID;
//
//public class RegionProtectionListener implements Listener {
//    private final WorldGuardContext context;
//
//    public RegionProtectionListener(WorldGuardContext context) {
//        this.context = context;
//    }
//
//    @EventHandler
//    public void onBlockBreak(BlockBreakEvent event) {
//        checkProtection(event.getBlock(), event.getPlayer(), event);
//    }
//
//    @EventHandler
//    public void onBlockPlace(BlockPlaceEvent event) {
//        checkProtection(event.getBlock(), event.getPlayer(), event);
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onRightClick(PlayerInteractEvent event) {
//        if (!event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
//            return;
//        }
//
//        checkProtection(event.getBlock(), event.getPlayer(), event);
//    }
//
//    @EventHandler
//    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
//        checkProtection(event.getBlockClicked(), event.getPlayer(), event);
//    }
//
//    @EventHandler
//    public void onItemFrameClick(ItemFrameDropItemEvent event) {
//        checkProtection(event.getBlock(), event.getPlayer(), event);
//    }
//
//    @EventHandler
//    public void onPvp(EntityDamageByEntityEvent event) {
//        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))
//            return;
//
//        Player damager = (Player) event.getDamager();
//        Player victim = (Player) event.getEntity();
//
//        if (pvpAllowed(damager) && pvpAllowed(victim))
//            return;
//
//        event.setCancelled();
//        Messages.NO_PVP.sendPopup(damager);
//    }
//
//    private void checkProtection(Block block, Player player, Event event) {
//        String regionName = context.findRtreeRegionName(block, block.getLevel().getId());
//        if (regionName == null) return;
//
//        List<UUID> allowedIds = context.getAllowedIds(regionName);
//        if (allowedIds.contains(player.getUniqueId()) || player.hasPermission("worldguard.god")) return;
//
//        event.setCancelled();
//        Messages.FOREIGN_RG.sendPopup(player);
//    }
//
//    private boolean pvpAllowed(Player player) {
//        String regionName = context.findRtreeRegionName(player.getPosition(), player.getLevel().getId());
//        if (regionName == null) return true;
//        return context.getFlagValue(regionName, "pvp");
//    }
//}
