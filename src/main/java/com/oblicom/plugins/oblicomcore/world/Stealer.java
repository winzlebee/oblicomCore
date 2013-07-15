package com.oblicom.plugins.oblicomcore.world;

import java.util.HashSet;
import java.util.Set;

import com.oblicom.plugins.oblicomcore.entity.Criminal;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author nagib.kanaan
 */
public class Stealer {
    private OblicomWorld world;
    private Set<String> data = new HashSet<String>();
    
    public Stealer () {
        this.world = OblicomWorld.getWorld();
    }
    
    /**
     * Add criminal to the list of stealer.
     * 
     * @param criminal
     * @param reason 
     */
    public void addToList(final Criminal criminal, int time) {
        String player = criminal.getPlayer().getName();
        
        data.add(player);
        
        world.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(world.getPlugin(), new Runnable() {
            public void run() {
                world.getStealer().removeFromList(criminal);
            }
        }, time * 60 * 20L); 
    }
    
    /**
     * Remove criminal from the list of stealer.
     * 
     * @param criminal
     */
    public void removeFromList(Criminal criminal) {
        data.remove(criminal.getPlayer().getName());
    }
    
    /**
     * Verify if the criminal is in stealer list
     * 
     * @param criminal
     * @return criminal is a stealer?
     */
    public boolean isInList(Criminal criminal) {
        return data.contains(criminal.getPlayer().getName());
    }
}