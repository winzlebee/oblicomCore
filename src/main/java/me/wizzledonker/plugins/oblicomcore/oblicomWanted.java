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
        
        return result;
    }
    
    public void addToList(Player killer, String reason) {
        getWantedConfig().set("wanted." + killer.getName() + ".reason", reason);
        saveWantedConfig();
    }
    
    public void removeFromList(Player killer) {
        getWantedConfig().set("wanted." + killer.getName(), null);
        saveWantedConfig();
    }
    
    public boolean isInList(Player killer) {
        return getWantedConfig().getConfigurationSection("wanted").contains(killer.getName());
    }
    
}
