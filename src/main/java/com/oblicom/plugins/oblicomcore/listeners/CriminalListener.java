package com.oblicom.plugins.oblicomcore.listeners;

import com.oblicom.plugins.oblicomcore.OblicomCore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.ChatColor;

import com.oblicom.plugins.oblicomcore.event.criminal.CriminalStealEvent;
import com.oblicom.plugins.oblicomcore.event.criminal.CriminalStealFailEvent;
import com.oblicom.plugins.oblicomcore.event.criminal.CriminalLockpickEvent;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import com.oblicom.plugins.oblicomcore.entity.Criminal;

import org.bukkit.block.Block;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author nagib.kanaan
 */
public class CriminalListener implements Listener {
    private OblicomCore plugin;
    
    public CriminalListener(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCriminalInteract(PlayerInteractEvent event) {
        Criminal criminal = new Criminal(event.getPlayer());
        
        Block block = event.getClickedBlock();
        
        if (criminal.actionIsValidToLockpick(event.getAction(), block) && event.isCancelled()) {
            if (criminal.lockpick(block)) {
                event.setCancelled(false);   
            }
        }
    }
    
    @EventHandler
    public void whenPlayerInteracts(PlayerInteractEntityEvent event) {
        Entity target = event.getRightClicked();
        
        if (target instanceof Player) {
            Criminal criminal = new Criminal(event.getPlayer());
            
            if (criminal.actionIsValidToSteal()) {
                criminal.steal(new Citizen((Player) target));
            }
        }
    }
    
    @EventHandler
    public void onCriminalSteal(CriminalStealEvent event) {
        System.out.println("Steal Listener " + event);
        
        double amountStolen = event.getAmount();
        final Criminal criminal = event.getCriminal();

        Citizen victim = event.getVictim();
        
        criminal.sendChatMessage(amountStolen == 0
                    ? ChatColor.DARK_GRAY + "After going through " + victim.getPlayer().getName() + "'s pockets, you find no cash."
                    : ChatColor.GREEN + "You have stolen " + ChatColor.WHITE + "$" + amountStolen + ChatColor.GREEN + " from " + victim.getPlayer().getName() + ". Now hide!");
    }
    
    @EventHandler
    public void onCriminalStealFail(CriminalStealFailEvent event) {
        System.out.println("Steal fail Listener " + event);
    }    

    @EventHandler
    public void onCriminalLockpick(CriminalLockpickEvent event) {
        System.out.println("Lockpick Listener " + event);
    }
}
