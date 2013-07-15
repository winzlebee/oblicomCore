package com.oblicom.plugins.oblicomcore.command;

import java.util.List;
import java.util.Arrays;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.oblicom.plugins.oblicomcore.OblicomCore;

import com.oblicom.plugins.oblicomcore.entity.Citizen;

import org.bukkit.entity.Player;

/**
 *
 * @author nagib.kanaan
 */
public class ReleaseCommand extends OblicomCommand {
    OblicomCore plugin;
    
    String requiredPermission = "oblicom.jail.unjail";
    String withoutPermissionMessage = ChatColor.RED + "No Permission!";
    
    public ReleaseCommand(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean validatedCommand(String command) {
        return command.equalsIgnoreCase("obliunjail");
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
        
        Player target = plugin.getServer().getPlayer(params[0]);
        if (target == null) {
           sender.sendMessage(ChatColor.DARK_RED +  params[0] + " is not online!");
           return false;
        }
        
        Citizen citizen = new Citizen(target);
        
        if (!citizen.isArrested()) {
           sender.sendMessage(ChatColor.RED + "Player " + params[0] + " isn't jailed!");
           return false;
        }
        
        citizen.release();
        sender.sendMessage(ChatColor.GREEN + "Player " + params[0] + " released.");
        
        return true;
    }
}