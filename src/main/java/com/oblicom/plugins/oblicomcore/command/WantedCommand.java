package com.oblicom.plugins.oblicomcore.command;

import java.util.List;
import java.util.Arrays;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.oblicom.plugins.oblicomcore.OblicomCore;

import org.bukkit.entity.Player;

/**
 *
 * @author nagib.kanaan
 */
public class WantedCommand extends OblicomCommand {
    OblicomCore plugin;
    
    String requiredPermission = "oblicom.wanted.view";
    String withoutPermissionMessage = ChatColor.RED + "You do not have permission to view the wanted list!";
    
    public WantedCommand(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean validatedCommand(String command) {
        return command.equalsIgnoreCase("wanted");
    }
    
    public boolean onOblicomCommand(CommandSender sender, Command command, String value, String[] params) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            if (!player.hasPermission(requiredPermission)) {
                player.sendMessage(withoutPermissionMessage);
                return false;
            }
        }
        
        if (params.length > 0) {
           sender.sendMessage(ChatColor.LIGHT_PURPLE + "This command takes no arguments!");
           return false;
        }
        
        sender.sendMessage(ChatColor.WHITE + "-----" + ChatColor.GREEN + "The Wanted List" + ChatColor.WHITE + "-----");        
        for (String wanted : OblicomCore.world.getWanted().getList()) {
            sender.sendMessage(wanted);
        }
        
        return true;
    }
}