package com.oblicom.plugins.oblicomcore.event.citizen;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author nagib.kanaan
 */
public class CitizenArrestEvent extends CitizenEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String arrestReason;
    private int arrestTime;
    private Boolean cancel;
    
    public CitizenArrestEvent(final Citizen citizenArrested, final String arrestReason, int arrestTime) {
        super(citizenArrested);
        this.arrestReason = arrestReason;
        this.arrestTime = arrestTime;
        this.cancel = false;
        
        System.out.println("Citizen Arrest");
    }

    /**
     * Gets the how long the the player still arrested
     *
     * @return arrest time
     */
    public int getTime() {
        return arrestTime;
    }
    
    /**
     * Gets the reason why the player is getting arrested
     *
     * @return arrest reason
     */
    public String getReason() {
        return arrestReason;
    }
    
    /**
     * Sets the reason why the player is getting arrested
     *
     * @param arrestReason arrest reason
     */
    public void setReason(String arrestReason) {
        this.arrestReason = arrestReason;
    }
        
    public boolean isCancelled() {
        return cancel;
    }
    
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }    
}
