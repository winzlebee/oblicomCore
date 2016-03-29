package me.wizzledonker.plugins.oblicomcore;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.wizzledonker.plugins.oblicomranks.OblicomRankScore;
import me.wizzledonker.plugins.oblicomranks.OblicomRanks;

/*
 * This is the main plugin for Oblicom, the minecraft server
 * it allows easy integration of the factions, and a gaol feature.
 * 
 * (C) WizzleDonker and Sn0wmatt 2012
 */

public class OblicomCore extends JavaPlugin {
    Economy economy = null;
    private Listener plistener = new oblicomPlayerListener(this);
    private Listener elistener = new oblicomEntityListener(this);
    
    public oblicomJail jail = new oblicomJail(this);
    public oblicomWanted wanted = new oblicomWanted(this);
    public OblicomRankScore scores = null;
    
    //Wanted checking time, in minutes
    public int wanted_check_time;
    public int wanted_time_kill;
    public int wanted_time_pickpocket;
    public int wanted_time_steal;
    
    //Payouts for the wanted list
    public Map<String, Double> wanted_payouts = new HashMap<String, Double>();
    public double wanted_minimum_bounty;
    
    //All the variables from the config
    public int chance;
    public int time;
    public int damage;
    public int max_amount;
    public int experience;
    public String humiliate_message = null;
    public int score;
    
    //Lockpick variables
    public int lockpick_chance;
    public int lockpick_damage;
    public int lockpick_item;
    public int lockpick_experience;
    public int lockpick_time;
    public int lockpick_score;
    
    //Jail variables
    public Location jail_location = null;
    public int jail_time;
    public double jail_bail;
    public String jail_message;
    public String jail_releasemessage;
    public int jail_score;
    public double jail_police_bonus;
    
    //Stick locator variables
    public int locator_time;
    
    //Award variables
    public int award_monster;
    public int award_passive;
    public int max_points;
    public double max_money;
    
    public double wanted_player_nerf;
    
    public Set<UUID> notAllowed = new HashSet<UUID>();
    public Map<UUID, Double> earnedCurrency = new HashMap<UUID, Double>();
    public Map<UUID, Integer> earnedPoints = new HashMap<UUID, Integer>();
    
    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here.
        log("plugin disabled");
    }

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        
        //Run the setupVault method and stop if the result is false
        if (!setupEconomy()) {
            log("Error enabling economy! Make sure the economy plugins + Vault are installed properly!");
            pm.disablePlugin(this);
            return;
        }
        
        if (!setupRanks()) {
            log("Error finding the oblicom ranks module! Make sure it is installed!");
            pm.disablePlugin(this);
            return;
        }
        //Register events for pickpocket + lockpick
        pm.registerEvents(plistener, this);
        //Register events for /wanted
        pm.registerEvents(elistener, this);
        
        //Commands
        getCommand("wanted").setExecutor(wanted);
        getCommand("unwanted").setExecutor(wanted);
        getCommand("addwanted").setExecutor(wanted);
        getCommand("addbounty").setExecutor(wanted);
        getCommand("removebounty").setExecutor(wanted);
        getCommand("oblisetjail").setExecutor(jail);
        getCommand("obliunjail").setExecutor(jail);
        getCommand("bail").setExecutor(jail);
        
        setupConfig();
        
        //Run a checker to make sure that people are removed from the wanted list
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

                public void run() {
                    //Check if each player's wanted time is over
                    for (String p : wanted.getWantedConfig().getConfigurationSection("wanted").getKeys(false)) {
                        long timeWhenUnwanted = wanted.getWantedConfig().getLong("wanted." + p + ".release_time");
                        if (timeWhenUnwanted <= System.currentTimeMillis()) {
                            wanted.removeFromList(p);
                            getServer().broadcastMessage(ChatColor.GOLD + "Player " + p + " is no longer wanted for anything.");
                        }
                    }
                }
                
            }, 0, 20L*60*this.wanted_check_time);
        
        //Every hour, reset the two maps containing the earned points of the currency
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

                public void run() {
                    earnedCurrency.clear();
                    earnedPoints.clear();
                }
                
            }, 0, 20L*60*60);
        
        log("plugin for oblicom.com has loaded.");
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
    
    private boolean setupRanks() {
        Plugin pl = getServer().getPluginManager().getPlugin("oblicomRanks");
        if (pl == null) {
            return false;
        }
        OblicomRanks oblicomRanks = (OblicomRanks) pl;
        scores = oblicomRanks.score;
        return (scores != null);
    }
    
    public void pickPocket(Player thief, Player victim) {
        Random rand = new Random();
        int cash = rand.nextInt(max_amount) + 1;
        
        if (economy.getBalance(victim) >= cash) {
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
        victim.sendMessage(ChatColor.RED + "You feel someone rummage through your pockets, faintly");
        scores.addScore(score, thief);
    }
    
    private void transferFunds(final Player thief, Player victim, int cash) {
        economy.withdrawPlayer(victim, cash);
        economy.depositPlayer(thief, cash);
        thief.sendMessage(ChatColor.GREEN + "You have stolen " + ChatColor.WHITE + "$" + cash +
                ChatColor.GREEN + " from " + victim.getName() + ". Now hide!");
    }
    
    public void setupConfig() {
        //Sets up and copies the oblicom config file.
        
        if (!new File(getDataFolder(), "config.yml").exists()) {
            //Set all the config defaults, and the header

            getConfig().options().header("Plugin by WizzleDonker."); 
            
            //Set the wanted list check time in minutes
            getConfig().addDefault("wanted.checktime", 30);
            getConfig().addDefault("wanted.pickpocketTime", 1);
            getConfig().addDefault("wanted.stealTime", 4);
            getConfig().addDefault("wanted.killTime", 8);
            
            //Set the default payout for killing a wanted player
            getConfig().addDefault("wanted.payouts.default", 500.0);
            getConfig().addDefault("wanted.minimum_bounty", 500.0);
            
            //Set the pickpocket defaults
            getConfig().addDefault("pickpocket.chance", 25);
            getConfig().addDefault("pickpocket.damage", 2);
            getConfig().addDefault("pickpocket.time", 2);
            getConfig().addDefault("pickpocket.max", 20);
            getConfig().addDefault("pickpocket.experience", 2);
            getConfig().addDefault("pickpocket.message", "%thief% attempted to steal from %victim%!");
            getConfig().addDefault("pickpocket.score", 25);
            
            //Set the lockpicking defaults
            getConfig().addDefault("lockpick.item", 265);
            getConfig().addDefault("lockpick.chance", 25);
            getConfig().addDefault("lockpick.damage", 3);
            getConfig().addDefault("lockpick.experience", 4);
            getConfig().addDefault("lockpick.time", 5);
            getConfig().addDefault("lockpick.score", 25);
            
            //Set the jail defaults
            getConfig().addDefault("jail.location.x", 10);
            getConfig().addDefault("jail.location.y", 10);
            getConfig().addDefault("jail.location.z", 10);
            getConfig().addDefault("jail.location.world", "world");
            getConfig().addDefault("jail.time", 5);
            getConfig().addDefault("jail.bail_amount", 250.0);
            getConfig().addDefault("jail.message", "You have been Jailed!");
            getConfig().addDefault("jail.releasemessage", "You have been Released!");
            getConfig().addDefault("jail.score", 50);
            getConfig().addDefault("jail.police_bonus", 2);
            List<String> commands = Arrays.asList("login", "register", "list");
            getConfig().addDefault("jail.allowed-commands", commands);
            
            getConfig().addDefault("locator.time", 30);
            
            //Add the award defaults
            getConfig().addDefault("award.monster", 15);
            getConfig().addDefault("award.player", 50);
            getConfig().addDefault("award.passive", 10);
            getConfig().addDefault("award.maxPointsPerHour", 50);
            getConfig().addDefault("award.maxMoneyPerHour", 100.0);
            getConfig().addDefault("award.wantedPlayerDamageNerf", 0.5);
            
            //TODO: Add defaults for other aspects of the plugin
            getConfig().options().copyHeader(true);
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        
        chance = getConfig().getInt("pickpocket.chance", 25);
        damage = getConfig().getInt("pickpocket.damage", 2);
        time = getConfig().getInt("pickpocket.time", 1);
        max_amount = getConfig().getInt("pickpocket.max", 20);
        experience = getConfig().getInt("pickpocket.experience", 2);
        humiliate_message = getConfig().getString("pickpicket.message", "%thief% attempted to steal from %victim%!");
        score = getConfig().getInt("pickpocket.score", 25);
        
        wanted_time_pickpocket = getConfig().getInt("wanted.pickpocketTime", 1);
        wanted_time_steal = getConfig().getInt("wanted.stealTime", 5);
        wanted_time_kill = getConfig().getInt("wanted.killTime", 8);
        this.wanted_check_time = getConfig().getInt("wanted.checktime", 30);
        
        for (String key : getConfig().getConfigurationSection("wanted.payouts").getKeys(false)) {
            this.wanted_payouts.put(key, getConfig().getDouble("wanted.payouts." + key));
        }
        wanted_minimum_bounty = getConfig().getDouble("wanted.minimum_bounty", 500.0);
        
        lockpick_damage = getConfig().getInt("lockpick.damage", 3);
        lockpick_item = getConfig().getInt("lockpick.item", 265);
        lockpick_chance = getConfig().getInt("lockpick.chance", 25);
        lockpick_experience = getConfig().getInt("lockpick.experience", 4);
        lockpick_time = getConfig().getInt("lockpick.time", 5);
        lockpick_score = getConfig().getInt("lockpick.score", 25);
        
        jail_location = new Location(this.getServer().getWorld(getConfig().getString("jail.location.world")),
                getConfig().getInt("jail.location.x"),
                getConfig().getInt("jail.location.y"),
                getConfig().getInt("jail.location.z"));
        jail_time = getConfig().getInt("jail.time");
        jail_bail = getConfig().getDouble("jail.bail_amount");
        jail_message = getConfig().getString("jail.message");
        jail_releasemessage = getConfig().getString("jail.releasemessage");
        jail_score = getConfig().getInt("jail.score", 50);
        jail_police_bonus = getConfig().getDouble("jail.police_bonus", 2);
        
        locator_time = getConfig().getInt("locator.time", 30);
        
        award_monster = getConfig().getInt("award.monster", 15);
        award_passive = getConfig().getInt("award.passive", 10);
        max_points = getConfig().getInt("award.maxPointsPerHour", 50);
        max_money = getConfig().getDouble("award.maxMoneyPerHour", 100.0);
        this.wanted_player_nerf = getConfig().getDouble("award.wantedPlayerDamageNerf", 0.5);
        
        log("finished loading/generating config file.");
    }
}
