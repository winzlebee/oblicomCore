/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.wizzledonker.plugins.oblicomcore;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Winfried
 */
public class oblicomJail implements CommandExecutor {
    private OblicomCore plugin = null;
    public Set<String> jailed = new HashSet<String>();
    
    public oblicomJail(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cmnd.getName().equalsIgnoreCase("oblisetjail")) {
            if (cs instanceof Player) {
                Player player = (Player) cs;
                if (!player.hasPermission("oblicom.jail.set")) {
                    player.sendMessage(ChatColor.RED + "No Permission!");
                    return true;
                }
                setJail(player);
                return true;
            }
        }
        if (cmnd.getName().equalsIgnoreCase("obliunjail")) {
            if (cs instanceof Player) {
                Player player = (Player) cs;
                if (!player.hasPermission("oblicom.jail.unjail")) {
                    player.sendMessage(ChatColor.RED + "No Permission!");
                    return true;
                }
            }
            if (strings.length == 1) {
                Player play = null;
                for (String key : jailed) {
                    if (key.equalsIgnoreCase(strings[0])) {
                        play = plugin.getServer().getPlayer(key);
                    }
                }
                if (play != null) {
                    releasePlayer(play);
                    cs.sendMessage(ChatColor.GREEN + play.getName() + " Released.");
                    return true;
                } else {
                    cs.sendMessage(ChatColor.RED + "Player isn't jailed!");
                    return true;
                }
            }
        }
        if (cmnd.getName().equalsIgnoreCase("bail")) {
            if (cs instanceof Player) {
                if (!cs.hasPermission("oblicom.jail.bail")) {
                    cs.sendMessage(ChatColor.RED + "You don't have permission to bail out of jail!");
                    return true;
                }
                Player player = (Player) cs;
                if (jailed.contains(player.getName())) {
                    if (plugin.economy.has(player.getName(), plugin.jail_bail)) {
                        plugin.economy.withdrawPlayer(player.getName(), plugin.jail_bail);
                        releasePlayer(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You don't have enough money to bail out. Rot!");
                    }
                } else {
                    player.sendMessage(ChatColor.AQUA + "You aren't jailed!");
                }
                return true;
            }
        }
        return false;
    }
    
        
    public void jailPlayer(Player player) {
        jailed.add(player.getName());
        player.sendMessage(ChatColor.RED + plugin.jail_message);
    }
    
    public void releasePlayer(Player player) {
        if (player.isOnline() && jailed.contains(player.getName())) {
            player.teleport(player.getWorld().getSpawnLocation());
            player.sendMessage(ChatColor.GREEN + plugin.jail_releasemessage);
        }
        jailed.remove(player.getName());
    }
    
    public void setJail(Player player) {
        plugin.getConfig().set("jail.location.x", player.getLocation().getX());
        plugin.getConfig().set("jail.location.y", player.getLocation().getY());
        plugin.getConfig().set("jail.location.z", player.getLocation().getZ());
        plugin.getConfig().set("jail.location.world", player.getLocation().getWorld().getName());
        plugin.saveConfig();
        plugin.setupConfig();
        player.sendMessage(ChatColor.GREEN + "The jail has been set to your location.");
    }
    
}
