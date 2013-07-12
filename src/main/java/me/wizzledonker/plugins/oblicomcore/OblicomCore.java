package me.wizzledonker.plugins.oblicomcore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * This is the main plugin for Oblicom, the minecraft server
 * it allows easy integration of the factions, and a gaol feature.
 * 
 * (C) WizzleDonker and Sn0wmatt 2012
 */

public class OblicomCore extends JavaPlugin {
    Economy economy = null;
    private PlayerListener plistener = new oblicomPlayerListener(this);
    private EntityListener elistener = new oblicomEntityListener(this);
    
    private CommandExecutor commandex = new oblicomCommands(this);
    public oblicomWanted wanted = new oblicomWanted(this);
    
    //All the variables from the config
    public int chance;
    public int time;
    public int damage;
    public int max_amount;
    public String humiliate_message = null;
    
    //Lockpick variables
    public int lockpick_chance;
    public int lockpick_damage;
    public int lockpick_item;
    public int lockpick_time;
    
    public Set<Player> notAllowed = new HashSet<Player>();
    
    public void onDisable() {
        // TODO: Place any custom disable code here.
        log("plugin disabled");
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        
        //Run the setupVault method and stop if the result is false
        if (!setupEconomy()) {
            log("Error enabling economy! Make sure the economy plugins + Vault are installed properly!");
            pm.disablePlugin(this);
            return;
        }
        //Register events for pickpocket
        pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, plistener, Priority.Normal, this);
        //Register events for lockpick
        pm.registerEvent(Type.PLAYER_INTERACT, plistener, Priority.Monitor, this);
        //Register events for /wanted
        pm.registerEvent(Type.ENTITY_DEATH, elistener, Priority.Monitor, this);
        
        //Commands
        getCommand("brules").setExecutor(commandex);
        getCommand("crules").setExecutor(commandex);
        getCommand("frules").setExecutor(commandex);
        
        //Other commands
        getCommand("wanted").setExecutor(wanted);
        
        setupConfig();
        
        log("plugin for oblicom.tk has loaded.");
    }
    
    public void log(String text) {
        System.out.println("[" + this + "] " + text);
    }
    
    private boolean setupEconomy() {
        Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
        if (vault == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        
        return (economy != null);
    }
    
    public void pickPocket(Player thief, Player victim) {
        Random rand = new Random();
        int cash = rand.nextInt(max_amount) + 1;
        
        if (economy.getBalance(victim.getName()) >= cash) {
            transferFunds(thief, victim, cash);
        } else {
            int victimBalance = (int) economy.getBalance(victim.getName());
            
            if (victimBalance == 0) {
                thief.sendMessage(ChatColor.DARK_GRAY + "After going through " + victim.getName() + "'s pockets, you find little or no cash.");
                return;
            }
            
            int remainder = rand.nextInt(victimBalance) + 1;
            transferFunds(thief, victim, remainder);
        }
        wanted.addToList(thief, "pickpocketing");
        
    }
    
    private void transferFunds(final Player thief, Player victim, int cash) {
        economy.withdrawPlayer(victim.getName(), cash);
        economy.depositPlayer(thief.getName(), cash);
        thief.sendMessage(ChatColor.GREEN + "You have stolen " + ChatColor.WHITE + "$" + cash +
                ChatColor.GREEN + " from " + victim.getName() + ". Now hide!");
        notAllowed.add(thief);
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                public void run() {
                    notAllowed.remove(thief);
                }
        }, time*60*20L);
    }
    
    public List<String> getRules(int type) {
        List<String> result = new ArrayList<String>();
        
        switch (type) {
            case 1:
                result.add("1. No cube/box buildings");
                result.add("2. In the city, build only on your lot");
                result.add("3. You may help people build, but only if allowed.");
                result.add("4. No building on the streets");
                result.add("5. Definitely, no griefing");
            case 2:
                result.add("1. Definitely no griefing");
                result.add("2. Be nice to moderators/staff");
                result.add("3. You are not a faction member");
                result.add("4. Excessive bad language will not be tolerated");
            case 3:
                result.add("1. No griefing to reach your goal");
                result.add("2. Faction members must be prompt");
                result.add("3. Faction members must follow orders");
                result.add("4. No abuse of faction powers will be tolerated.");
            case 4:
                result = getConfig().getList("brules");
            case 5:
                result = getConfig().getList("crules");
            case 6:
                result = getConfig().getList("frules");
        }
        
        return result;
    }
    
    private void setupConfig() {
        //Sets up and copies the oblicom config file.
        
        if (!new File(getDataFolder(), "config.yml").exists()) {
            //Set all the config defaults, and the header

            getConfig().options().header("Main oblicom file configuration, everything you need is here.");
            getConfig().options().header("Plugin by WizzleDonker."); 
            
            //Set the pickpocket defaults
            getConfig().addDefault("pickpocket.chance", 25);
            getConfig().addDefault("pickpocket.damage", 2);
            getConfig().addDefault("pickpocket.time", 2);
            getConfig().addDefault("pickpocket.max", 20);
            getConfig().addDefault("pickpocket.message", "%thief% attempted to steal from %victim%!");
            
            //Set the lockpicking defaults
            getConfig().addDefault("lockpick.item", 265);
            getConfig().addDefault("lockpick.chance", 25);
            getConfig().addDefault("lockpick.damage", 3);
            getConfig().addDefault("lockpick.time", 5);
            
            //Set the custom list commands
            getConfig().addDefault("brules", getRules(1));
            getConfig().addDefault("crules", getRules(2));
            getConfig().addDefault("frules", getRules(3));
            
            //TODO: Add defaults for other aspects of the plugin
            getConfig().options().copyHeader(true);
            getConfig().options().copyDefaults(true);
        }
        
        chance = getConfig().getInt("pickpocket.chance", 25);
        damage = getConfig().getInt("pickpocket.damage", 2);
        time = getConfig().getInt("pickpocket.time", 1);
        max_amount = getConfig().getInt("pickpocket.max", 20);
        humiliate_message = getConfig().getString("pickpicket.message", "%thief% attempted to steal from %victim%!");
        
        lockpick_damage = getConfig().getInt("lockpick.damage", 3);
        lockpick_item = getConfig().getInt("lockpick.item", 265);
        lockpick_chance = getConfig().getInt("lockpick.chance", 25);
        lockpick_time = getConfig().getInt("lockpick.time", 5);
        
        log("finished loading/generating config file.");
        
        saveConfig();
    }
}
