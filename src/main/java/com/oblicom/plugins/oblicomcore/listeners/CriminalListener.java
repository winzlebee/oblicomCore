package com.oblicom.plugins.oblicomcore.listeners;

import com.oblicom.plugins.oblicomcore.OblicomCore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.ChatColor;

import com.oblicom.plugins.oblicomcore.event.criminal.CriminalStealEvent;
import com.oblicom.plugins.oblicomcore.event.criminal.CriminalStealFailEvent;
import com.oblicom.plugins.oblicomcore.event.criminal.CriminalLockpickEvent;
import com.oblicom.plugins.oblicomcore.event.criminal.CriminalLockpickFailEvent;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import com.oblicom.plugins.oblicomcore.entity.Criminal;

import org.bukkit.block.Block;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import org.bukkit.Material;

/**
 *
 * @author nagib.kanaan
 */
public class CriminalListener implements Listener {
    private OblicomCore plugin;
    
    HashMap<String,String> stealReasons = new HashMap<String,String>();
    HashMap<String,String> lockpickReasons = new HashMap<String,String>();
    
    public CriminalListener(OblicomCore plugin) {
        this.plugin = plugin;
        
        stealReasons.put("without_permission", ChatColor.DARK_GRAY + "You find yourself unable to steal money.");
        stealReasons.put("without_experience", ChatColor.YELLOW + "You don't have enough experience to pickpocket. You need level " + OblicomCore.configuration.getInt("criminal.pickpocket.experience"));
        stealReasons.put("steal_recently", ChatColor.RED + "Cool it! You can't steal that often!");       
        stealReasons.put("victim_sealed", ChatColor.RED + "Thou shall not steal from this person!");
        // stealReasons.put("victim_alerted", "");

        lockpickReasons.put("without_permission", ChatColor.DARK_GRAY + "You find yourself unable to lockpick.");
        lockpickReasons.put("without_experience", ChatColor.YELLOW + "You don't have enough experience to rob chests. You need level " + OblicomCore.configuration.getInt("criminal.lockpick.experience"));
        lockpickReasons.put("wrong_item", ChatColor.AQUA + "Use a " + Material.getMaterial(OblicomCore.configuration.getInt("criminal.lockpick.item")) + " to lockpick!");
        lockpickReasons.put("lockpick_recently", ChatColor.RED + "Cool it! You can't pick locks that often!");
        lockpickReasons.put("lockpick_fail", ChatColor.RED + "You fiddle with the lock and eventually fail, hurting your finger.");
        
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
        double amountStolen = event.getAmount();
        final Criminal criminal = event.getCriminal();

        Citizen victim = event.getVictim();
        
        criminal.sendChatMessage(amountStolen == 0
                    ? ChatColor.DARK_GRAY + "After going through " + victim.getPlayer().getName() + "'s pockets, you find no cash."
                    : ChatColor.GREEN + "You have stolen " + ChatColor.WHITE + "$" + amountStolen + ChatColor.GREEN + " from " + victim.getPlayer().getName() + ". Now hide!");
    }
    
    @EventHandler
    public void onCriminalStealFail(CriminalStealFailEvent event) {
        String reason = event.getReason();
        
        if (stealReasons.containsKey(reason)) {
            event.getCriminal().sendChatMessage(stealReasons.get(reason));
        }
    }    

    @EventHandler
    public void onCriminalLockpick(CriminalLockpickEvent event) {
        event.getCriminal().sendChatMessage(ChatColor.DARK_GREEN + "Success! You have picked this lock.");
        plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Fear spreads through the land as news of a lockpick arrives!");
    }

    @EventHandler
    public void onCriminalLockpickFail(CriminalLockpickFailEvent event) {
        String reason = event.getReason();
        
        if (lockpickReasons.containsKey(reason)) {
            event.getCriminal().sendChatMessage(lockpickReasons.get(reason));
        }
    }
}
