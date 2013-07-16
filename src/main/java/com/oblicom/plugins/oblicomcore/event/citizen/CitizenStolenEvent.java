package com.oblicom.plugins.oblicomcore.event.citizen;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import com.oblicom.plugins.oblicomcore.entity.Criminal;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author nagib.kanaan
 */
public class CitizenStolenEvent extends CitizenEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean alerted;
    private Criminal criminalSteal;
    private boolean cancel;
    
    public CitizenStolenEvent(final Citizen citizenStolen, Criminal criminalSteal, boolean alerted) {
        super(citizenStolen);
        this.alerted = alerted;
        this.criminalSteal = criminalSteal;
        this.cancel = false;
        
        System.out.println("Citizen Stolen");
    }

    /**
     * Criminal trying to steal
     *
     * @return criminal
     */
    public Criminal getCriminal() {
        return criminalSteal;
    }
    
    /**
     * Is victim alerted about the criminal
     *
     * @return is alerted
     */
    public boolean isAlerted() {
        return alerted;
    }
    
    /**
     * Sets the alerted
     *
     * @param alerted about criminal
     */
    public void setAlerted(boolean alerted) {
        this.alerted = alerted;
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

