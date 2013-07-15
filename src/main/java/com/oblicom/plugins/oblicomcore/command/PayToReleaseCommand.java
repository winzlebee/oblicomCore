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
public class PayToReleaseCommand extends OblicomCommand {
    OblicomCore plugin;
    
    String requiredPermission = "oblicom.jail.bail";
    String withoutPermissionMessage = ChatColor.RED + "You don't have permission to bail out of jail!";
    
    public PayToReleaseCommand(OblicomCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean validatedCommand(String command) {
        return command.equalsIgnoreCase("bail");
    }
    
    public boolean onOblicomCommand(CommandSender sender, Command command, String value, String[] params) {
        if (sender instanceof Player) {
            if (!sender.hasPermission(requiredPermission)) {
                sender.sendMessage(withoutPermissionMessage);
                return false;
            }
            
            if (params.length > 0) {
               sender.sendMessage(ChatColor.LIGHT_PURPLE + "This command takes no arguments!");
               return false;
            }            
            
            Citizen citizen = new Citizen((Player) sender);

            if (!citizen.isArrested()) {
               sender.sendMessage(ChatColor.AQUA + "You aren't jailed!");
               return false;
            }
            
            Double priceToRelease = OblicomCore.configuration.getDouble("police.jail.bail_amount");
            
            if (!citizen.hasMoney(priceToRelease)) {
               sender.sendMessage(ChatColor.RED + "You don't have enough money to bail out. Rot!");
               return false;
            }
            
            if (citizen.takeMoney(priceToRelease) != priceToRelease) {
               sender.sendMessage(ChatColor.RED + "We were unable to withdraw money from your account.!");
               OblicomCore.log("Warning: Error to withdraw money from " + sender.getName());
               return false;
            }
            
            citizen.release();
        }
        
        sender.sendMessage(ChatColor.RED + "You must be a player!");
        return false;
    }
}

//public class oblicomJail implements CommandExecutor {
//    private OblicomCore plugin = null;
//    public Set<String> jailed = new HashSet<String>();
//    
//    public oblicomJail(OblicomCore plugin) {
//        this.plugin = plugin;
//    }
//    
//    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {

//        if (cmnd.getName().equalsIgnoreCase("bail")) {
//            if (cs instanceof Player) {
//                if (!cs.hasPermission("oblicom.jail.bail")) {
//                    cs.sendMessage(ChatColor.RED + );
//                    return true;
//                }
//                Player player = (Player) cs;

//                if (jailed.contains(player.getName())) {
//                    if (plugin.economy.has(player.getName(), plugin.jail_bail)) {
//                        plugin.economy.withdrawPlayer(player.getName(), plugin.jail_bail);
//                        releasePlayer(player);
//                    } else {
//                        player.sendMessage(ChatColor.RED + "You don't have enough money to bail out. Rot!");
//                    }
//                } else {
//                    player.sendMessage(ChatColor.AQUA + "");
//                }
//                return true;
//            }
//        }
//        return false;
//    }
//    
//        