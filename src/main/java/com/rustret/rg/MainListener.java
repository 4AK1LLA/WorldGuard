package com.rustret.rg;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import com.rustret.rg.objects.Region;
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
    public void onWandSelect(PlayerInteractEvent event) {
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

        // to prevent wood blocks strip
        event.setCancelled();
    }

    // /ec command might not work in regions
    @EventHandler
    public void onChestOpenProtection(InventoryOpenEvent e) {
        Inventory inventory = e.getInventory();
        if (inventory.getType() == InventoryType.PLAYER) {
            return;
        }
        Player player = e.getPlayer();
        Position pos = (Position) inventory.getHolder();
        if (!allowed(player, pos)) {
            e.setCancelled();
        }
    }

    @EventHandler
    public void onBlockPlaceProtection(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (allowed(player, e.getBlock())
                && allowed(player, e.getBlockAgainst())) {
            return;
        }
        e.setCancelled();
    }

    @EventHandler
    public void onBlockBreakProtection(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!allowed(player, e.getBlock())) {
            e.setCancelled();
        }
    }

    // doors sometimes glitching (stay open)
    @EventHandler
    public void onDoorToggleProtection(DoorToggleEvent e) {
        Player player = e.getPlayer();
        if (!allowed(player, e.getBlock())) {
            e.setCancelled();
        }
    }

    @EventHandler
    public void onBucketEmptyProtection(PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();
        if (!allowed(player, e.getBlockClicked())) {
            e.setCancelled();
        }
    }

    @EventHandler
    public void onBucketFillProtection(PlayerBucketFillEvent e) {
        Player player = e.getPlayer();
        if (!allowed(player, e.getBlockClicked())) {
            e.setCancelled();
        }
    }

    @EventHandler
    public void onEntityInteractProtection(PlayerInteractEntityEvent e) {
        if (e.getEntity() instanceof EntityCreature) return;

        Player player = e.getPlayer();
        if (!allowed(player, e.getEntity())) {
            e.setCancelled();
        }
    }

    @EventHandler
    public void onEntityDestroyProtection(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            if (e.getEntity() instanceof EntityCreature) return;

            Player player = (Player) e.getDamager();
            if (!allowed(player, e.getEntity())) {
                e.setCancelled();
            }
        }
    }

    @EventHandler
    public void onItemFrameProtection(ItemFrameDropItemEvent e) {
        Player player = e.getPlayer();
        if (!allowed(player, e.getBlock())) {
            e.setCancelled();
        }
    }

    @EventHandler
    public void onBlockIgniteProtection(BlockIgniteEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (!allowed(player, e.getBlock())) {
                e.setCancelled();
            }
        }
    }

    @EventHandler
    public void onFlameBowProtection(ProjectileHitEvent e) {
        if (e.getEntity() instanceof EntityArrow && e.getEntity().isOnFire()
                && e.getMovingObjectPosition().typeOfHit == 0) {
            e.getEntity().extinguish();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractProtection(PlayerInteractEvent e) {
        Item item = e.getItem();
        Block block = e.getBlock();
        Player player = e.getPlayer();

        if (e.getAction() == PlayerInteractEvent.Action.PHYSICAL) {
            if (block instanceof BlockPressurePlateBase) {
                if (allowed(player, block)) {
                    return;
                }
                e.setCancelled();
            }
        }
        else if (e.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            if (block.getId() == Block.NOTEBLOCK) {
                if (allowed(player, block)) {
                    return;
                }
                e.setCancelled();
                return;
            }
            Block fire = block.getSide(e.getFace());
            if (fire.getId() == Block.FIRE) {
                if (allowed(player, block)) {
                    return;
                }
                e.setCancelled();
                block.level.sendBlocks(new Player[]{player}, new Vector3[]{fire});
            }
        }
        else if (e.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (
                    item.getId() == Item.WOODEN_AXE && item.hasEnchantment(-1) // checking rg on wand
                    || item.getId() == Item.DYE && item.getDamage() == 0x0F && block.canBeActivated() // bone meal
                    || block instanceof BlockBed
                    || block.getId() == Block.NOTEBLOCK
                    || block.getId() == Block.JUKEBOX && item instanceof ItemRecord
                    || block.getId() == Block.FLOWER_POT_BLOCK
                    || block.getId() == 461 // bell
                    || block.getId() == Block.END_PORTAL_FRAME && item.getId() == Item.ENDER_EYE
                    || block instanceof BlockRedstoneDiode // comparator, repeater
                    || block instanceof BlockButton
                    || block.getId() == Block.LEVER
                    || block instanceof BlockWood && item.isAxe()
                    || block instanceof BlockDirt && item.isHoe()
                    || block.getId() == Block.PUMPKIN && item.isShears()
                    || block.getId() == Block.MONSTER_SPAWNER && item instanceof ItemSpawnEgg
                    || block.getId() == Block.DRAGON_EGG
                    || block.getId() == Block.ITEM_FRAME_BLOCK
                    || item.getId() == Item.END_CRYSTAL && (block.getId() == Block.OBSIDIAN || block.getId() == Block.BEDROCK)
                    || item.getId() == Item.PAINTING && (e.getFace() != BlockFace.UP && e.getFace() != BlockFace.DOWN)
                    || block.getId() == Block.TNT && (item.getId() == Item.FLINT_STEEL || item.getId() == Item.FIRE_CHARGE
                            || item.hasEnchantment(Enchantment.ID_FIRE_ASPECT))
                    || item.getId() == Item.BOAT
                    || block instanceof BlockRail && item.getId() == Item.MINECART
                    || item instanceof ItemBucket // preventing waterlogging bugs
                    || block.getId() == Block.ENDER_CHEST
            ) {

                if (!allowed(player, block)) {
                    e.setCancelled();
                }
            }
        }
    }

    private boolean allowed(Player player, Position pos) {
        Region rg = storage.intersectedRegion(pos);
        if (rg == null
                || player.hasPermission("rg.god")
                || rg.owner.equals(player.getUniqueId())
                || rg.members.contains(player.getUniqueId())) {
            return true;
        }
        if (!rg.secret) {
            player.sendPopup(messages.PROTECT_INTERACT);
            //player.level.addSound(pos, Sound.BLOCK_FALSE_PERMISSIONS, 1.0F, 1.0F, player);
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPvpProtection(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player
                && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();

            if (damager.hasPermission("rg.god")) return;
            if (pvpAllowed(damager) && pvpAllowed(e.getEntity())) return;

            e.setCancelled();
            damager.sendMessage(messages.PROTECT_PVP);
        }
    }

    private boolean pvpAllowed(Position pos) {
        Region rg = storage.intersectedRegion(pos.floor());
        return rg == null || rg.pvp;
    }
}
