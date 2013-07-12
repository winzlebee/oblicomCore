/*
 * Oblicom player listener (C) WizzleDonker 2012
 */
package me.wizzledonker.plugins.oblicomcore;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class oblicomPlayerListener extends PlayerListener{
    private static OblicomCore plugin;
    
    public oblicomPlayerListener(OblicomCore instance) {
        plugin = instance;
    }
    
    private Set<Player> noLockPick = new HashSet<Player>();
    
    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity targetEntity = event.getRightClicked();
        Random r = new Random();
        
        if (targetEntity instanceof Player) {
            Player thief = event.getPlayer();
            Player victim = (Player) event.getRightClicked();
            
            if (thief.hasPermission("oblicom.pickpocket.steal")) {
                if (victim.hasPermission("oblicom.pickpocket.nosteal")) {
                    thief.sendMessage(ChatColor.RED + "Thou shall not steal from this person!");
                    return;
                }    
                if (thief.getItemInHand().getType() == Material.AIR) {
                    if (plugin.notAllowed.contains(thief)) {
                        thief.sendMessage(ChatColor.DARK_RED + "Cool it! You can't steal that often.");
                        return;
                    }
                    if (r.nextInt(100) <= plugin.chance) {
                        plugin.pickPocket(thief, victim);
                    } else {
                        pickPocketAlert(thief, victim);
                    }
                }
            } else {
                thief.sendMessage(ChatColor.DARK_GRAY + "You find yourself unable to steal money.");
                return;
            }
            
        }
        
        
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!player.hasPermission("oblicom.lockpick.pick")) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block block = event.getClickedBlock();
        if (!block.getType().equals(Material.CHEST)) {
            return;
        }
        if (!event.isCancelled()) return;
        if (player.getItemInHand().getTypeId() != plugin.lockpick_item) {
            player.sendMessage("Wrong item for lock-picking!");
            return;
        }
        if (noLockPick.contains(player)) {
            player.sendMessage(ChatColor.RED + "Cool it! You can't steal that often!");
            return;
        }
        Random rand = new Random();
        if (rand.nextInt(100) <= plugin.lockpick_chance) {
            player.sendMessage(ChatColor.DARK_GREEN + "Success! You have picked this lock.");
            block = block.getRelative(BlockFace.UP);
            block.setType(Material.WALL_SIGN);
            block.setData(block.getRelative(BlockFace.DOWN).getData());
            Sign signblock = (Sign) block.getState();
            signblock.setLine(0, "This lock has");
            signblock.setLine(1, "been picked.");
            signblock.update(true);
            event.setCancelled(false);
            plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Fear spreads through the land as news of a lockpick arrives!");
            plugin.wanted.addToList(player, "theft");
        } else {
            player.sendMessage(ChatColor.RED + "You fiddle with the lock and eventually fail, hurting your finger.");
            player.damage(plugin.lockpick_damage);
            player.setItemInHand(null);
        }
        noLockPick.add(player);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    noLockPick.remove(player);
                }
        }, plugin.lockpick_time*60*20L);
    }
    
    private void pickPocketAlert(Player thief, Player victim) {
        String alertMsg = plugin.humiliate_message;
        
        alertMsg = alertMsg.replace("%thief%", thief.getName());
        alertMsg = alertMsg.replace("%victim%", victim.getName());
        
        plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + alertMsg);
        thief.damage(plugin.damage);
    }
    
}
