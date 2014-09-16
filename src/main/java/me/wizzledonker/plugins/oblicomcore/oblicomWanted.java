/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.wizzledonker.plugins.oblicomcore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Win
 */
public class oblicomWanted implements CommandExecutor {
    private static OblicomCore plugin;
    
    private File wantedConfigFile = null;
    private FileConfiguration wantedConfig = null;
    
    public oblicomWanted(OblicomCore instance) {
        plugin = instance;
        
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cmnd.getName().equalsIgnoreCase("wanted")) {
            if (cs instanceof Player) {
                Player player = (Player) cs;
                if (!player.hasPermission("oblicom.wanted.view")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to view the wanted list!");
                    return true;
                }
            }
            cs.sendMessage(ChatColor.WHITE + "-----" + ChatColor.GREEN + "The Wanted List" + ChatColor.WHITE + "-----");
            for (String s : getWantedList()) {
                cs.sendMessage(s);
            }
            return true;
        }
        if (cmnd.getName().equalsIgnoreCase("unwanted")) {
            if (cs instanceof Player) {
                Player player = (Player) cs;
                if (!player.hasPermission("oblicom.wanted.remove")) {
                    player.sendMessage("You don't have permission to remove players from the wanted list!");
                    return true;
                }
            }
            if (strings.length == 1) {
                if (isInList(strings[0])) {
                    removeFromList(strings[0]);
                    cs.sendMessage(ChatColor.GREEN + "Player " + strings[0] + " is no longer wanted.");
                } else {
                    cs.sendMessage(ChatColor.RED + strings[0] + " isn't on the wanted list!");
                }
                return true;
            }
        }
        if (cmnd.getName().equalsIgnoreCase("addbounty")) {
            if (cs instanceof Player) {
                Player player = (Player) cs;
                if (!player.hasPermission("oblicom.wanted.addbounty")) {
                    player.sendMessage("You don't have permission to add some bounty!");
                    return true;
                }
                if (strings.length == 2) {
                    if (isInList(strings[0])) {
                        try {
                            int amount = Integer.parseInt(strings[1]);
                            if (amount >= plugin.wanted_minimum_bounty) {
                                if (plugin.economy.has(player, amount)) {
                                    plugin.economy.withdrawPlayer(player, amount);
                                    player.sendMessage(ChatColor.GREEN + "You have added " + ChatColor.WHITE
                                            + plugin.economy.format(amount) + ChatColor.GREEN + " to " + ChatColor.WHITE + strings[0] + "'s bounty!");
                                    this.addBounty(strings[0], amount);
                                } else {
                                    player.sendMessage(ChatColor.RED + "You don't have that much money");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The minimum bounty to add is " + ChatColor.WHITE + plugin.economy.format(plugin.wanted_minimum_bounty));
                            }
                        } catch (NumberFormatException ex) {
                            player.sendMessage(ChatColor.RED + "Your bounty payout must be a number");
                        }
                    } else {
                        cs.sendMessage(ChatColor.RED + strings[0] + " isn't on the wanted list!");
                    }
                    return true;
                }
            } else {
                cs.sendMessage(ChatColor.RED + "Only players can add bounty");
            }
        }
        if (cmnd.getName().equalsIgnoreCase("removebounty")) {
            if (cs instanceof Player) {
                Player player = (Player) cs;
                if (!player.hasPermission("oblicom.wanted.removebounty")) {
                    player.sendMessage("You don't have permission to add some bounty!");
                    return true;
                }
            }
            if (strings.length == 2) {
                if (isInList(strings[0])) {
                    try {
                        int amount = Integer.parseInt(strings[1]);
                        cs.sendMessage(ChatColor.GREEN + "You have removed " + ChatColor.WHITE
                            + plugin.economy.format(amount) + ChatColor.GREEN + " from " + ChatColor.WHITE + strings[0] + "'s bounty!");
                        this.removeBounty(strings[0], amount);
                    } catch (NumberFormatException ex) {
                        cs.sendMessage(ChatColor.RED + "Your bounty payout must be a number");
                    }
                } else {
                    cs.sendMessage(ChatColor.RED + strings[0] + " isn't on the wanted list!");
                }
                return true;
            }
        }
        if (cmnd.getName().equalsIgnoreCase("addwanted")) {
            if (cs instanceof Player) {
                Player player = (Player) cs;
                if (!player.hasPermission("oblicom.wanted.add")) {
                    player.sendMessage("You don't have permission to add players to the wanted list!");
                    return true;
                }
            }
            if (strings.length == 3) {
                if (!isInList(strings[0])) {
                    try {
                        addToList(strings[0], strings[1], Integer.parseInt(strings[2]));
                    } catch (NumberFormatException ex) {
                        cs.sendMessage(ChatColor.RED + "The last argument must be a number! (no spaces in the reason)");
                    }
                    cs.sendMessage(ChatColor.GREEN + "Player " + strings[0] + " is now wanted for " + strings[1]);
                } else {
                    cs.sendMessage(ChatColor.RED + strings[0] + " is already wanted!");
                }
                return true;
            }
        }
        return false;
    }
    
    public void reloadWantedConfig() {
        if (wantedConfigFile == null) {
            wantedConfigFile = new File(plugin.getDataFolder(), "wanted.yml");
        }
        wantedConfig = YamlConfiguration.loadConfiguration(wantedConfigFile);
    }
    
    public FileConfiguration getWantedConfig() {
        if (wantedConfig == null) {
            reloadWantedConfig();
        }
        return wantedConfig;
    }
    
    public void saveWantedConfig() {
        if (wantedConfigFile == null || wantedConfig == null) {
            return;
        }
        try {
            wantedConfig.save(wantedConfigFile);
        } catch (IOException ex) {
            plugin.log("oops! Error saving wanted config file. Here's the details: " + ex.toString());
        }
    }
    
    public List<String> getWantedList() {
        List<String> result = new ArrayList<String>();
        
        for (String p : getWantedConfig().getConfigurationSection("wanted").getKeys(false)) {
            result.add(ChatColor.AQUA + plugin.economy.format(getWantedPayout(p)) + ": " + ChatColor.WHITE + p + " for " + getWantedConfig().getString("wanted." + p + ".reason")
                    + ChatColor.BOLD + ChatColor.GRAY + " (" + getDaysToRelease(p) + (getDaysToRelease(p) == 1 ? " Day)" : " Days)"));
        }
        
        if (result.isEmpty()) {
            result.add("Nobody is currently wanted for anything!");
        }
        
        return result;
    }
    
    public double getWantedPayout(String p) {
        double initial = plugin.wanted_payouts.get(getWantedConfig().getString("wanted." + p + ".reason")) != null ?
                plugin.wanted_payouts.get(getWantedConfig().getString("wanted." + p + ".reason")) :
                plugin.wanted_payouts.get("default");
        return initial + getRawWantedPayout(p);
    }
    
    private double getRawWantedPayout(String p) {
        return getWantedConfig().getDouble("wanted." + p + ".payout");
    }
    
    public void addBounty(String player, double amount) {
        double original = getWantedConfig().getDouble("wanted." + player + ".payout");
        getWantedConfig().set("wanted." + player + ".payout", amount+original);
    }
    
    public void removeBounty(String player, double amount) {
        double original = getWantedConfig().getDouble("wanted." + player + ".payout");
        getWantedConfig().set("wanted." + player + ".payout", original-amount);
    }
    
    public int getDaysToRelease(String p) {
        return Math.round((this.getWantedConfig().getLong("wanted." + p + ".release_time")-System.currentTimeMillis())/1000/60/60/24);
    }
    
    public void addToList(String killer, String reason, int days) {
        //Add to the bounty if already on the list
        if (isInList(killer)) {
            days += getDaysToRelease(killer);
            getWantedConfig().set("wanted." + killer + ".payout", getWantedPayout(killer));
        }
        
        getWantedConfig().set("wanted." + killer + ".reason", reason);
        long finalDate = System.currentTimeMillis() + (days*24*60*60*1000);
        getWantedConfig().set("wanted." + killer + ".release_time", finalDate);
        saveWantedConfig();
    }
    
    public void removeFromList(String killer) {
        getWantedConfig().set("wanted." + killer, null);
        saveWantedConfig();
    }
    
    public boolean isInList(String killer) {
        return getWantedConfig().getConfigurationSection("wanted").contains(killer);
    }
    
}
