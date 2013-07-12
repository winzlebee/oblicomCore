/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.wizzledonker.plugins.oblicomcore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        if (cmnd.getName().equalsIgnoreCase("addwanted")) {
            if (cs instanceof Player) {
                Player player = (Player) cs;
                if (!player.hasPermission("oblicom.wanted.add")) {
                    player.sendMessage("You don't have permission to add players to the wanted list!");
                    return true;
                }
            }
            if (strings.length == 2) {
                if (!isInList(strings[0])) {
                    addToList(strings[0], strings[1]);
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
            result.add(p + " for " + getWantedConfig().getString("wanted." + p + ".reason"));
        }
        
        if (result.isEmpty()) {
            result.add("Nobody is currently wanted for anything!");
        }
        
        return result;
    }
    
    public void addToList(String killer, String reason) {
        getWantedConfig().set("wanted." + killer + ".reason", reason);
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
