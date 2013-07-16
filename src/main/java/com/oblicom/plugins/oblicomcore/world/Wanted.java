package com.oblicom.plugins.oblicomcore.world;

import java.util.ArrayList;
import java.util.List;
import com.oblicom.plugins.oblicomcore.OblicomCore;
import com.oblicom.plugins.oblicomcore.entity.Citizen;
import com.oblicom.plugins.oblicomcore.exceptions.OblicomWorldException;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author nagib.kanaan
 */
public class Wanted {
    private OblicomWorld world;
    private ConfigurationSection data;
    
    public Wanted () {
        this.world = OblicomWorld.getWorld();
        this.data = world.getData().getConfigurationSection("police.wanted.citizens");
        
        if (data == null) {
            data = world.getData().createSection("police.wanted.citizens");
        }
    }   
    
    /**
     * Get the list of wanted citizens.
     * 
     * @return wanted citizens
     */
    public List<String> getList() {
        List<String> result = new ArrayList<String>();
        
        for (String citizen : data.getKeys(false)) {
            result.add(citizen + " for " + data.getString(citizen + ".reason") + "(Due date " + data.getString(citizen + ".date") + ")");
        }
        
        if (result.isEmpty()) {
            result.add("Nobody is currently wanted for anything!");
        }
        
        return result;
    }
    
    /**
     * Add Citizen to the list of wanted players.
     * 
     * @param citizen
     * @param reason 
     * @param time
     */
    public void addToList(Citizen citizen, String reason, int time) {
        String player = citizen.getPlayer().getName();
        
        data.set(player + ".reason", reason);
        data.set(player + ".time", time); // TODO ADD TIME
        
        try {
            world.save();
        } catch (OblicomWorldException error) {
            OblicomCore.log("Warning: error to save wanted data in file: " + error.getMessage());
        }
    }
    
    /**
     * Remove Citizen from the list of wanted players.
     * 
     * @param citizen
     */
    public void removeFromList(Citizen citizen) {
        data.set(citizen.getPlayer().getName(), null);

        try {
            world.save();
        } catch (OblicomWorldException error) {
            OblicomCore.log("Warning: error to save wanted data in file: " + error.getMessage());
        }
    }
    
    /**
     * Verify if the citizen is in wanted list
     * 
     * @param citizen
     * @return citizen is wanted?
     */
    public boolean isInList(Citizen citizen) {
        return data.contains(citizen.getPlayer().getName());
    }
    
    /**
     * Verify if the citizen is in wanted list by name
     * 
     * @param name
     * @return player is wanted?
     */
    public boolean isInList(String name) {
        return data.contains(name);
    }
}
