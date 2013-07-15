package com.oblicom.plugins.oblicomcore.world;

import com.oblicom.plugins.oblicomcore.entity.Police;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author nagib.kanaan
 */
public class Tracker {
    private OblicomWorld world;
    private Set<String> data = new HashSet<String>();
    
    public Tracker () {
        this.world = OblicomWorld.getWorld();
    }   
    
    /**
     * Add to the list the police has used tracker.
     * 
     * @param police
     * @param time
     */
    public void addToList(final Police police, int time) {
        String player = police.getPlayer().getName();
        
        data.add(player);
        
        world.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(world.getPlugin(), new Runnable() {
            public void run() {
                world.getTracker().removeFromList(police);
            }
        }, time * 60 * 20L);         
    }
    
    /**
     * Remove from the list the police has used tracker.
     * 
     * @param police
     */
    public void removeFromList(Police police) {
        data.remove(police.getPlayer().getName());
    }
    
    /**
     * Verify if the police is in the list
     * 
     * @param police
     * @return police is used tracker?
     */
    public boolean isInList(Police police) {
        return data.contains(police.getPlayer().getName());
    }
    
    /**
     * Verify if the police is in the list by name
     * 
     * @param name
     * @return police is used tracker?
     */
    public boolean isInList(String name) {
        return data.contains(name);
    }
}
