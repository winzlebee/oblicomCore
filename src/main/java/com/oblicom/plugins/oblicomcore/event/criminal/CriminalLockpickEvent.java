package com.oblicom.plugins.oblicomcore.event.criminal;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Criminal;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author nagib.kanaan
 */
public class CriminalLockpickEvent extends CriminalEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Boolean cancel;
    
    public CriminalLockpickEvent(final Criminal criminalLockpicking) {
        super(criminalLockpicking);
        this.cancel = false;
        
        System.out.println("Criminal lockpick");
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

