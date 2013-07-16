package com.oblicom.plugins.oblicomcore.event.police;

import com.oblicom.plugins.oblicomcore.entity.Police;
import org.bukkit.event.Event;

/**
 * Represents a police related event
 */
public abstract class PoliceEvent extends Event {
    protected Police police;

    public PoliceEvent(final Police who) {
        police = who;
    }

    PoliceEvent(final Police who, boolean async) {
        super(async);
        police = who;

    }

    /**
     * Returns the police involved in this event
     *
     * @return Police who is involved in this event
     */
    public final Police getPolice() {
        return police;
    }
}
