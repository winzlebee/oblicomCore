package com.oblicom.plugins.oblicomcore.event.police;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.oblicom.plugins.oblicomcore.entity.Police;
import com.oblicom.plugins.oblicomcore.entity.Citizen;

/**
 *
 * @author nagib.kanaan
 */
public class PoliceWantedRemoveEvent extends PoliceEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Citizen wantedCitizen;
    private Boolean cancel;
    
    public PoliceWantedRemoveEvent(final Police policeWantRemoving, Citizen wantedCitizen) {
        super(policeWantRemoving);
        this.wantedCitizen = wantedCitizen;
        this.cancel = false;
        
        System.out.println("Police Remove Wanted");
    }
    
    /**
     * Gets the wanted citizen
     *
     * @return wanted citizen
     */
    public Citizen getCitizen() {
        return wantedCitizen;
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

