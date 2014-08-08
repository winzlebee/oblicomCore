//Listener for /wanted command (C) WizzleDonker and Oblicom
package me.wizzledonker.plugins.oblicomcore;

import java.util.Random;
import java.util.logging.Level;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class oblicomEntityListener implements Listener {
    private static OblicomCore plugin;
    
    public oblicomEntityListener(OblicomCore instance) {
        plugin = instance;
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            
            if (damaged.hasPermission("oblicom.pvp.neutral") || damager.hasPermission("oblicom.pvp.neutral")) {
                event.setCancelled(true);
            }
        } 
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void whenEntityDies(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Entity killed = event.getEntity();
        if (killed instanceof Monster) {
            plugin.scores.addScore(plugin.award_monster, event.getEntity().getKiller());
            return;
        }
        if (killed instanceof Animals) {
            plugin.scores.addScore(plugin.award_passive, event.getEntity().getKiller());
            return;
        }
        if (killed instanceof Player) {
            final Player killedPlayer = (Player) killed;
            Entity killer = killedPlayer.getKiller();
            if (killer instanceof Player) {
                Player pkiller = (Player) killer;
                Random rand = new Random();
                if (plugin.wanted.isInList(killedPlayer.getName())) {
                    plugin.wanted.removeFromList(killedPlayer.getName());
                    plugin.jail.jailPlayer(killedPlayer);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            plugin.jail.releasePlayer(killedPlayer);
                        }
                    }, plugin.jail_time*60*20L);
                    plugin.scores.subtractScore(plugin.jail_score, killedPlayer);
                    if (pkiller.hasPermission("oblicom.wanted.exempt")) {
                        plugin.scores.addScore(plugin.jail_score * 2, pkiller);
                        return;
                    }
                }
                plugin.scores.addScore(plugin.jail_score, pkiller);
                if (pkiller.hasPermission("oblicom.wanted.exempt")) {
                    plugin.scores.subtractScore(plugin.jail_score * 2, pkiller);
                }
                if (pkiller.hasPermission("oblicom.wanted.fullexempt")) {
                    return;
                }
                if (rand.nextBoolean()) {
                    plugin.wanted.addToList(pkiller.getName(), "murder");
                } else {
                    plugin.wanted.addToList(pkiller.getName(), "manslaughter");
                }
            }
        }
    }
    
}