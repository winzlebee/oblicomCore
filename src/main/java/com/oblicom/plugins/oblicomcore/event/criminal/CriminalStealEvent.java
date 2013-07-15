package com.oblicom.plugins.oblicomcore.event.criminal;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Criminal;
import com.oblicom.plugins.oblicomcore.entity.Citizen;

/**
 *
 * @author nagib.kanaan
 */
public class CriminalStealEvent extends CriminalEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Citizen victim;
    private double amountStolen;
    private Boolean cancel;
    
    public CriminalStealEvent(final Criminal criminalStealing, final Citizen victim,  double amountStolen) {
        super(criminalStealing);
        this.amountStolen = amountStolen;
        this.victim = victim;
        this.cancel = false;
        
        System.out.println("Criminal Steal");
    }
    
    /**
     * Gets the criminal victim
     *
     * @return victim stolen
     */
    public Citizen getVictim() {
        return victim;
    }
    
    /**
     * Gets the amount stolen from the victim
     *
     * @return amount stolen
     */
    public double getAmount() {
        return amountStolen;
    }
    
    /**
     * Sets the amount stolen from the victim
     *
     * @param amountStolen amount stolen
     */
    public void setAmount(double amountStolen) {
        this.amountStolen = amountStolen;
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

