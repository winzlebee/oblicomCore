/*
 * Oblicom entity listener (C) WizzleDonker 2012
 */
package com.oblicom.plugins.oblicomcore.listeners;

import java.util.Random;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import com.oblicom.plugins.oblicomcore.OblicomCore;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityListener implements Listener {
    private OblicomCore plugin;
    
    public EntityListener(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        
        if (damaged instanceof Player && damager instanceof Player) {
            
            if (((Player)damaged).hasPermission("oblicom.pvp.neutral") || ((Player)damager).hasPermission("oblicom.pvp.neutral")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void whenEntityDies(EntityDeathEvent event) {
        Entity killer = event.getEntity().getKiller();
        
        if (killer == null || !(killer instanceof Player)) {
            return;
        }
                
        Entity killed = event.getEntity();
        Citizen citizenKiller = new Citizen((Player) killer);
        
        if (killed instanceof Monster) {
            citizenKiller.addScore(OblicomCore.configuration.getInt("award.monster"));
            return;
        }
        
        if (killed instanceof Animals) {
            citizenKiller.addScore(OblicomCore.configuration.getInt("award.passive"));
            return;
        }
        
        if (killed instanceof Player) {
            final Citizen citizenKilled = new Citizen((Player) killed);

            if (citizenKilled.isWanted()) {
                citizenKilled.unwanted();
                citizenKilled.arrest("killed when wanted");
                
                citizenKilled.subtractScore(OblicomCore.configuration.getInt("pocile.jail.score"));
                
                if (citizenKiller.getPlayer().hasPermission("oblicom.wanted.exempt")) {
                    citizenKiller.addScore(OblicomCore.configuration.getInt("award.passive") * 2);
                    return;
                }
            }
            
            citizenKiller.addScore(OblicomCore.configuration.getInt("pocile.jail.score"));
                
            if (citizenKiller.getPlayer().hasPermission("oblicom.wanted.exempt")) {
                citizenKiller.subtractScore(OblicomCore.configuration.getInt("pocile.jail.score") * 2);
            }
            
            if (citizenKiller.getPlayer().hasPermission("oblicom.wanted.fullexempt")) {
                return;
            }
            
            citizenKiller.wanted(new Random().nextBoolean() ? "murder" : "manslaughter", OblicomCore.configuration.getInt("police.wanted.expire"));
        }
    }
    
}
