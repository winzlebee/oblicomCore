package com.oblicom.plugins.oblicomcore.event.citizen;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author nagib.kanaan
 */
public class CitizenWantedEvent extends CitizenEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String wantedtReason;
    private int wantedTime;
    private Boolean cancel;
    
    public CitizenWantedEvent(final Citizen citizenWanted, String wantedtReason, int wantedTime) {
        super(citizenWanted);
        this.wantedtReason = wantedtReason;
        this.wantedTime = wantedTime;
        this.cancel = false;
        
        System.out.println("Citizen Wanted");
    }

    /**
     * Gets the how long the the player still wanted
     *
     * @return arrest time
     */
    public int getTime() {
        return wantedTime;
    }
    
    /**
     * Gets the reason why the player is in wanted list.
     *
     * @return string wanted reason
     */
    public String getReason() {
        return wantedtReason;
    }
    
    /**
     * Sets the reason why the player is in wanted list.
     *
     * @param wantedtReason wanted reason
     */
    public void setReason(String wantedtReason) {
        this.wantedtReason = wantedtReason;
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

