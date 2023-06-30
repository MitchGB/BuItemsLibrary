package com.bu.buitemslibrary.eventhandler;

import com.bu.buitemslibrary.AbstractItem;
import org.bukkit.event.Event;

/**
 * Represents any event handler method within an abstract item
 * @param <T>
 */
public abstract class EventContext<T extends Event> {

    public final void accept(AbstractItem handler, T event){
        //this.apply((T)tag, itemStack);
    }
    public abstract void apply(T event);

}
