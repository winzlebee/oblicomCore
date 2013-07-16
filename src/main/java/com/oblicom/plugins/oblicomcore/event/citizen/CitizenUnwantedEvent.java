package com.oblicom.plugins.oblicomcore.event.citizen;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author nagib.kanaan
 */
public class CitizenUnwantedEvent extends CitizenEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Boolean cancel;
    
    public CitizenUnwantedEvent(final Citizen citizenUnwanted) {
        super(citizenUnwanted);
        this.cancel = false;
        
        System.out.println("Citizen Unwanted");
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

