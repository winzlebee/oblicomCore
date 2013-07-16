package com.oblicom.plugins.oblicomcore.event.citizen;

import com.oblicom.plugins.oblicomcore.entity.Citizen;
import org.bukkit.event.Event;

/**
 * Represents a citizen related event
 */
public abstract class CitizenEvent extends Event {
    protected Citizen citizen;

    public CitizenEvent(final Citizen who) {
        citizen = who;
    }

    CitizenEvent(final Citizen who, boolean async) {
        super(async);
        citizen = who;

    }

    /**
     * Returns the citizen involved in this event
     *
     * @return Citizen who is involved in this event
     */
    public final Citizen getCitizen() {
        return citizen;
    }
}
