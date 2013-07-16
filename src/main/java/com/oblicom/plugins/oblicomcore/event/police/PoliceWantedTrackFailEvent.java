package com.oblicom.plugins.oblicomcore.event.police;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Police;

/**
 *
 * @author nagib.kanaan
 */
public class PoliceWantedTrackFailEvent extends PoliceEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String failReason;
    private Boolean cancel;
    
    public PoliceWantedTrackFailEvent(final Police policeTracking, final String failReason) {
        super(policeTracking);
        this.failReason = failReason;
        this.cancel = false;
        
        System.out.println("Police Wanted Track Fail: " + failReason);
    }

    /**
     * Gets the reason why the player is failed to steal
     *
     * @return string arrest reason
     */
    public String getReason() {
        return failReason;
    }
    
    /**
     * Sets the reason why the player is failed to steal
     *
     * @param failReason fail reason
     */
    public void setReason(String failReason) {
        this.failReason = failReason;
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

