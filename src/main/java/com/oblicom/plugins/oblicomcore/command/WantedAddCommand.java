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
public class WantedAddCommand extends OblicomCommand {
    OblicomCore plugin;
    
    String requiredPermission = "oblicom.wanted.add";
    String withoutPermissionMessage = ChatColor.RED + "You don't have permission to add players to the wanted list!";
    
    public WantedAddCommand(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean validatedCommand(String command) {
        return command.equalsIgnoreCase("addwanted");
    }
    
    public boolean onOblicomCommand(CommandSender sender, Command command, String value, String[] params) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            if (!player.hasPermission(requiredPermission)) {
                player.sendMessage(withoutPermissionMessage);
                return false;
            }
        }
        
        if (params.length > 2) {
           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Too many arguments!");
           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Arguments must be: <player> <reason>!");
           return false;
        }
        
        if (params.length < 2) {
           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Not enough arguments!");
           sender.sendMessage(ChatColor.LIGHT_PURPLE + "Arguments must be: <player> <reason>!");
           return false;
        }
        
        Player target = (Player) plugin.getServer().getOfflinePlayer(params[0]);
        
        Citizen citizen = new Citizen(target);
        
        if (citizen.isWanted()) {
           sender.sendMessage(ChatColor.RED + "Player " + params[0] + " already wanted!");
           return false;
        }
        
        citizen.wanted(params[1]);
        
        sender.sendMessage(ChatColor.GREEN + "Player " + params[0] + " is  now wanted for" + params[1] + ".");
        
        return true;
    }
}