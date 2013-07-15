package com.oblicom.plugins.oblicomcore.event.citizen;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author nagib.kanaan
 */
public class CitizenReleaseEvent extends CitizenEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Boolean cancel;
    
    public CitizenReleaseEvent(final Citizen citizenReleased) {
        super(citizenReleased);
        this.cancel = false;
        
        System.out.println("Citizen Realase");
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

