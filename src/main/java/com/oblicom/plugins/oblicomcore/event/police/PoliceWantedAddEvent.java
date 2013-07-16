package com.oblicom.plugins.oblicomcore.event.police;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Police;
import com.oblicom.plugins.oblicomcore.entity.Citizen;

/**
 *
 * @author nagib.kanaan
 */
public class PoliceWantedAddEvent extends PoliceEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Citizen wantedCitizen;
    private String wantedtReason;
    private int wantedTime;
    private Boolean cancel;
    
    public PoliceWantedAddEvent(final Police policeWantAdding, Citizen wantedCitizen, String wantedtReason, int wantedTime) {
        super(policeWantAdding);
        this.wantedCitizen = wantedCitizen;
        this.wantedtReason = wantedtReason;
        this.wantedTime = wantedTime;
        this.cancel = false;
        
        System.out.println("Police Add Wanted");
    }

    /**
     * Gets the wanted time
     *
     * @return wanted time
     */
    public int getTime() {
        return wantedTime;
    }    
    
    /**
     * Gets the wanted citizen
     *
     * @return wanted citizen
     */
    public Citizen getCitizen() {
        return wantedCitizen;
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

