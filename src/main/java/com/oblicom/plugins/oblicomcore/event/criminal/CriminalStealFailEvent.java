package com.oblicom.plugins.oblicomcore.event.criminal;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Criminal;

/**
 *
 * @author nagib.kanaan
 */
public class CriminalStealFailEvent extends CriminalEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String failReason;
    private Boolean cancel;
    
    public CriminalStealFailEvent(final Criminal criminalStealing, final String failReason) {
        super(criminalStealing);
        this.failReason = failReason;
        this.cancel = false;
        
        System.out.println("Criminal steal fail: " + failReason);
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

