package com.oblicom.plugins.oblicomcore.event.criminal;

import com.oblicom.plugins.oblicomcore.entity.Criminal;
import org.bukkit.event.Event;

/**
 * Represents a criminal related event
 */
public abstract class CriminalEvent extends Event {
    protected Criminal criminal;

    public CriminalEvent(final Criminal who) {
        criminal = who;
    }

    CriminalEvent(final Criminal who, boolean async) {
        super(async);
        criminal = who;

    }

    /**
     * Returns the criminal involved in this event
     *
     * @return Criminal who is involved in this event
     */
    public final Criminal getCriminal() {
        return criminal;
    }
}
