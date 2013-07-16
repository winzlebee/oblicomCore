package com.oblicom.plugins.oblicomcore.world;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.oblicom.plugins.oblicomcore.OblicomCore;
import com.oblicom.plugins.oblicomcore.entity.Citizen;
//import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author nagib.kanaan
 */
public class Wanted {
    private OblicomWorld world;
    
    public Wanted () {
        world = OblicomWorld.getWorld();
    }   
    
    /**
     * Get the list of wanted citizens.
     * 
     * @return wanted citizens
     */
    public List<String> getList() {
        List<String> result = new ArrayList<String>();
        
        try {
            ResultSet data = OblicomCore.database.query("SELECT * FROM wanted WHERE status = 1");
            
            while(data.next()) {
                result.add(data.getString("player") + " for " + data.getString("reason"));
            }
            
            if (result.isEmpty()) {
                result.add("Nobody is currently wanted for anything!");
            }
        } catch(SQLException error) {
            OblicomCore.log(error.getMessage());
            result.add("Error to load wanted list. Please report to a staff!");
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
        int date = time; // calculate the expire time.
        
        try {
             OblicomCore.database.insert("INSERT INTO wanted (player, reason, status, date) VALUES ('" + player + "', '" + reason + "', 1, " + date + ")");
        } catch(SQLException error) {
            OblicomCore.log("Error to add wanted player! " + error.getMessage());
        }        
    }
    
    /**
     * Remove Citizen from the list of wanted players.
     * 
     * @param citizen
     */
    public void removeFromList(Citizen citizen) {
        try {
             OblicomCore.database.query("UPDATE wanted SET status = 0 WHERE player = '" + citizen.getPlayer().getName() + "'");
        } catch(SQLException error) {
            OblicomCore.log("Error to update wanted player! " + error.getMessage());
        }
    }
    
    /**
     * Verify if the citizen is in wanted list
     * 
     * @param citizen
     * @return citizen is wanted?
     */
    public boolean isInList(Citizen citizen) {
        return isInList(citizen.getPlayer().getName());
    }
    
    /**
     * Verify if the citizen is in wanted list by name
     * 
     * @param name
     * @return player is wanted?
     */
    public boolean isInList(String name) {
        try {
             ResultSet data = OblicomCore.database.query("SELECT * FROM wanted WHERE player = '" + name + "' AND status = 1");
             return data.next();
        } catch(SQLException error) {
            OblicomCore.log("Error to select wanted player! " + error.getMessage());
            return false;
        }
    }
}
