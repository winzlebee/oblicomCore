//Listener for /wanted command (C) WizzleDonker and Oblicom
package me.wizzledonker.plugins.oblicomcore;

import java.util.Random;
import java.util.logging.Level;
import org.bukkit.ChatColor;
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
        Random rand = new Random();
        if (killed instanceof Monster) {
            addPointsAndCheck(event.getEntity().getKiller(), plugin.award_monster);
            addCurrencyAndCheck(event.getEntity().getKiller(), plugin.award_monster, rand);
            return;
        }
        if (killed instanceof Animals) {
            addPointsAndCheck(event.getEntity().getKiller(), plugin.award_passive);
            addCurrencyAndCheck(event.getEntity().getKiller(), plugin.award_passive, rand);
            return;
        }
        if (killed instanceof Player) {
            final Player killedPlayer = (Player) killed;
            Entity killer = killedPlayer.getKiller();
            if (killer instanceof Player) {
                Player pkiller = (Player) killer;
                double payout = 0;
                if (plugin.wanted.isInList(killedPlayer.getName())) {
                    payout = plugin.wanted.getWantedPayout(killedPlayer.getName());
                    plugin.wanted.removeFromList(killedPlayer.getName());
                    plugin.jail.jailPlayer(killedPlayer);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            plugin.jail.releasePlayer(killedPlayer.getUniqueId());
                        }
                    }, plugin.jail_time*60*20L);
                    plugin.scores.subtractScore(plugin.jail_score, killedPlayer);
                    plugin.economy.withdrawPlayer(killedPlayer, payout);
                    killedPlayer.sendMessage(ChatColor.RED + "You have lost " + ChatColor.WHITE + plugin.economy.format(payout) + ChatColor.RED + " as bounty.");
                    if (pkiller.hasPermission("oblicom.wanted.exempt")) {
                        plugin.scores.addScore((int) plugin.jail_police_bonus * plugin.jail_score, pkiller);
                        plugin.economy.depositPlayer(pkiller, plugin.jail_police_bonus * payout);
                        pkiller.sendMessage(ChatColor.GREEN + "You have recieved " + ChatColor.WHITE + plugin.economy.format(plugin.jail_police_bonus * payout) + ChatColor.GREEN + " as bounty.");
                        return;
                    }
                }
                plugin.scores.addScore(plugin.jail_score, pkiller);
                if (payout > 0) {
                    pkiller.sendMessage(ChatColor.GREEN + "You have recieved " + ChatColor.WHITE + plugin.economy.format(payout) + ChatColor.GREEN + " as bounty.");
                    plugin.economy.depositPlayer(pkiller, payout);
                }
                if (pkiller.hasPermission("oblicom.wanted.exempt")) {
                    plugin.scores.subtractScore((int) (plugin.jail_score * plugin.jail_police_bonus), pkiller);
                    plugin.economy.withdrawPlayer(pkiller, plugin.jail_score * plugin.jail_police_bonus);
                    pkiller.sendMessage(ChatColor.RED + "You have lost " + ChatColor.WHITE + plugin.economy.format(plugin.jail_police_bonus * plugin.jail_score) + ChatColor.RED + " for killing an innocent.");
                }
                if (pkiller.hasPermission("oblicom.wanted.fullexempt")) {
                    return;
                }
                if (rand.nextBoolean()) {
                    plugin.wanted.addToList(pkiller.getName(), "murder", plugin.wanted_time_kill);
                } else {
                    plugin.wanted.addToList(pkiller.getName(), "manslaughter", plugin.wanted_time_kill/2);
                }
            }
        }
    }
    
    public void addPointsAndCheck(Player killerPlayer, int points) {
        int earnedPoints = plugin.earnedPoints.get(killerPlayer.getUniqueId()) == null ? 0 : plugin.earnedPoints.get(killerPlayer.getUniqueId());
        if (earnedPoints < plugin.max_points) {
            plugin.scores.addScore(points, killerPlayer);
            plugin.earnedPoints.put(killerPlayer.getUniqueId(),
                    plugin.earnedPoints.get(killerPlayer.getUniqueId()) == null ? points : plugin.earnedPoints.get(killerPlayer.getUniqueId())+points);
        }
    }
    
    public void addCurrencyAndCheck(Player killerPlayer, double baseRate, Random rand) {
        //Temporary measures for early economy
        double finalAmount = rand.nextDouble()*2*baseRate;
        if (plugin.earnedCurrency.containsKey(killerPlayer.getUniqueId())) {
            double initialAmount = plugin.earnedCurrency.get(killerPlayer.getUniqueId());
            double factor = (plugin.max_money - initialAmount)/plugin.max_money;
            //If the amount for multiplication is higher than zero, perform it, otherwhise leave the player with nothing
            finalAmount = finalAmount * (factor > 0 ? factor : 0);
            plugin.earnedCurrency.put(killerPlayer.getUniqueId(), initialAmount+finalAmount);
        } else {
            plugin.earnedCurrency.put(killerPlayer.getUniqueId(), finalAmount);
        }
        
        if (finalAmount > 0.009) {
            plugin.economy.depositPlayer(killerPlayer, finalAmount);
            killerPlayer.sendMessage(ChatColor.GOLD + "Oblicom Initiative: " + ChatColor.WHITE + plugin.economy.format(finalAmount) + " earned.");
        }
    }
    
}
