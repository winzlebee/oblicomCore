package com.oblicom.plugins.oblicomcore.command;

import java.util.List;
import java.util.Arrays;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.oblicom.plugins.oblicomcore.OblicomCore;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import com.oblicom.plugins.oblicomcore.event.citizen.CitizenReleaseEvent;

import org.bukkit.entity.Player;

/**
 *
 * @author nagib.kanaan
 */
public class WantedRemoveCommand extends OblicomCommand {
    OblicomCore plugin;
    
    String requiredPermission = "oblicom.wanted.remove";
    String withoutPermissionMessage = ChatColor.RED + "You don't have permission to remove players from the wanted list!";
    
    public WantedRemoveCommand(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean validatedCommand(String command) {
        return command.equalsIgnoreCase("unwanted");
    }
    
    public boolean onOblicomCommand(CommandSender sender, Command command, String value, String[] params) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            if (!player.hasPermission(requiredPermission)) {
                player.sendMessage(withoutPermissionMessage);
                return false;
            }
        }
        
        if (params.length > 1) {
           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Too many arguments!");
           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Arguments must be: <player>!");
           return false;
        }
        
        if (params.length < 1) {
           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Not enough arguments!");
           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Arguments must be: <player>!");
           return false;
        }
        
        Player target = (Player) plugin.getServer().getOfflinePlayer(params[0]);
        
        Citizen citizen = new Citizen(target);
        
        if (!citizen.isWanted()) {
           sender.sendMessage(ChatColor.RED + "Player " + params[0] + " isn't on the wanted list!");
           return false;
        }
        
        citizen.unwanted();
        
        sender.sendMessage(ChatColor.GREEN + "Player " + params[0] + " is no longer wanted.");
        
        return true;
    }
}