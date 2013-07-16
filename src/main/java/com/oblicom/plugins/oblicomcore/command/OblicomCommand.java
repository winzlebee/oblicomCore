package com.oblicom.plugins.oblicomcore.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author nagib.kanaan
 */
public abstract class OblicomCommand implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command command, String value, String[] params) {
        
        String commandValue = command.getName();
        
        if (!validatedCommand(commandValue)) {
            return false;
        }
        
        return onOblicomCommand(sender, command, value, params);
    }
    
    public abstract boolean onOblicomCommand(CommandSender sender, Command command, String value, String[] params);
    public abstract boolean validatedCommand(String commandName);
}
