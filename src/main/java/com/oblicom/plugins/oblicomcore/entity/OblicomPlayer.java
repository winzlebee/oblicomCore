package com.oblicom.plugins.oblicomcore.entity;

import com.oblicom.plugins.oblicomcore.world.OblicomWorld;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.entity.Player;

import com.oblicom.plugins.oblicomcore.OblicomCore;


public abstract class OblicomPlayer {
    protected final PluginManager pluginManager;
    protected final Player player;
    
    protected OblicomWorld world;
    protected YamlConfiguration configuration;
    
    public OblicomPlayer(Player player) {
        this.player = player;
        this.pluginManager = player.getServer().getPluginManager();
        
        this.world = OblicomCore.world;
        this.configuration = OblicomCore.configuration;
    }
    
    /**
     * Show a message to the player
     * 
     * @param message Message to be displayed
     */    
    public void sendChatMessage(String message) {
        player.sendMessage(message);
    }
    
    /**
     * Show a multiple message to the player
     * 
     * @param message Message to be displayed
     */    
    public void sendChatMessage(String[] message) {
        player.sendMessage(message);
    }
}