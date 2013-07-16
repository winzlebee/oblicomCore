package com.oblicom.plugins.oblicomcore.entity;

import org.bukkit.entity.Player;

import com.oblicom.plugins.oblicomcore.event.police.PoliceWantedAddEvent;
import com.oblicom.plugins.oblicomcore.event.police.PoliceWantedRemoveEvent;
import com.oblicom.plugins.oblicomcore.event.police.PoliceWantedTrackEvent;
import com.oblicom.plugins.oblicomcore.event.police.PoliceWantedTrackFailEvent;

import org.bukkit.Material;
import org.bukkit.event.block.Action;

public class Police extends Citizen {
 
    public Police(Player player) {
        super(player);
    }

    /**
     * Add the citizen to the wanted list.
     * 
     * @param citizen the citizen
     */    
    public void wantedCitizen(Citizen citizen, String reason) {
        citizen.wanted(reason);
        
        pluginManager.callEvent(new PoliceWantedAddEvent(this, citizen, reason, configuration.getInt("police.wanted.expire")));
    }

    /**
     * Remove the citizen from the wanted list.
     * 
     * @param citizen the citizen
     */    
    public void unwantedCitizen(Citizen citizen) {
        citizen.unwanted();
        
        pluginManager.callEvent(new PoliceWantedRemoveEvent(this, citizen));
    }

    /**
     * Track wanted players.
     * 
     */    
    public void trackWantedPlayers() {
        if (player.hasPermission("oblicom.wanted.locator")) {
            pluginManager.callEvent(new PoliceWantedTrackFailEvent(this, "without_permission"));
            return;
        }

        if (world.getTracker().isInList(this)) {
            pluginManager.callEvent(new PoliceWantedTrackFailEvent(this, "tracked_recently"));
            return;
        }
        
        for (Player other : player.getServer().getOnlinePlayers()) {
            if (world.getWanted().isInList(other.getName())) {
                other.getWorld().strikeLightningEffect(other.getLocation());
            }
        }
        
        world.getTracker().addToList(this, configuration.getInt("police.tracker.time"));
        
        pluginManager.callEvent(new PoliceWantedTrackEvent(this));
    }
    
    /**
     * Condition to execute the track wanted players.
     * 
     */    
    public boolean actionIsValidToTrackWantedPlayers(Action action) {
        return action.equals(Action.RIGHT_CLICK_AIR) && player.getItemInHand().getType().equals(Material.STICK);
    }
}