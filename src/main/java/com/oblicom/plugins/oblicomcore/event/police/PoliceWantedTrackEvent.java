package com.oblicom.plugins.oblicomcore.event.police;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Police;

/**
 *
 * @author nagib.kanaan
 */
public class PoliceWantedTrackEvent extends PoliceEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Boolean cancel;
    
    public PoliceWantedTrackEvent(final Police police) {
        super(police);
        this.cancel = false;
        
        System.out.println("Police Track Wanted");
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

