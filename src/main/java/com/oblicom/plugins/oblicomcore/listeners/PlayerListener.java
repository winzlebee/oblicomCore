/*
 * Oblicom player listener (C) WizzleDonker 2012
 */
package com.oblicom.plugins.oblicomcore.listeners;

import com.oblicom.plugins.oblicomcore.OblicomCore;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
//import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private OblicomCore plugin;
    
    public PlayerListener(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    private Set<String> noLockPick = new HashSet<String>();
    private Set<String> noLocatorStick = new HashSet<String>();
    
    // @EventHandler(priority = EventPriority.HIGHEST)
    // public void onPlayerJoin(PlayerJoinEvent event) {
    // TODO - store/retrieve information about the user like: score, password, home. lots...
    // }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        if (OblicomCore.world.getJail().isInList(player.getName())) {
            OblicomCore.log("A player was jailed!");
            event.setRespawnLocation(OblicomCore.world.getJail().getLocation());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        
        if (OblicomCore.world.getJail().isInList(player.getName())) {
            player.sendMessage("You are jailed! You can't break blocks!");
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].replaceFirst("/", "");

        if (OblicomCore.configuration.getStringList("police.jail.allowed-commands").contains(command)) {
            return;
        }
        
        if (OblicomCore.world.getJail().isInList(player.getName())) {
            player.sendMessage("You are jailed! You can't use commands!");
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            
            if (event.isCancelled() && OblicomCore.world.getWanted().isInList(damaged.getName())) {
                event.setCancelled(false);
            }
            
            if (OblicomCore.world.getJail().isInList(damager.getName())) {
                damager.sendMessage("You are jailed! You can't hurt inmates!");
                damaged.sendMessage(ChatColor.RED + "You were abused by " + damager.getName());
                
                event.setCancelled(true);
            }
        }
    }
}
