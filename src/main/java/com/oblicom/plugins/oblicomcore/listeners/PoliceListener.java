package com.oblicom.plugins.oblicomcore.listeners;

import com.oblicom.plugins.oblicomcore.OblicomCore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.ChatColor;

import com.oblicom.plugins.oblicomcore.event.police.PoliceWantedAddEvent;
import com.oblicom.plugins.oblicomcore.event.police.PoliceWantedRemoveEvent;
import com.oblicom.plugins.oblicomcore.event.police.PoliceWantedTrackEvent;
import com.oblicom.plugins.oblicomcore.event.police.PoliceWantedTrackFailEvent;

import com.oblicom.plugins.oblicomcore.entity.Police;

/**
 *
 * @author nagib.kanaan
 */
public class PoliceListener implements Listener {
    private OblicomCore plugin;
    
    public PoliceListener(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPoliceInteract(PlayerInteractEvent event) {        
        Police police = new Police(event.getPlayer());
        
        if (police.actionIsValidToTrackWantedPlayers(event.getAction())) {
            police.trackWantedPlayers();
        }
    }
        
    @EventHandler
    public void onPoliceAddWanted(PoliceWantedAddEvent event) {
        System.out.println("Add wanted Listener " + event);
    }

    @EventHandler
    public void onPoliceRemoveWanted(PoliceWantedRemoveEvent event) {
        System.out.println("Remove wanted Listener " + event);
    }

    @EventHandler
    public void onPoliceTrackWanted(PoliceWantedTrackEvent event) {
        event.getPolice().sendChatMessage(ChatColor.GOLD + "Follow the lightning for wanted players!");
    }    

    @EventHandler
    public void onPoliceTrackWantedFail(PoliceWantedTrackFailEvent event) {
        System.out.println("Police wanted track fail Listener " + event);
    }        
}
