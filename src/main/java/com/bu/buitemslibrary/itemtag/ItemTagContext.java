package com.bu.buitemslibrary.itemtag;

import com.bu.buitemslibrary.AbstractItem;
import org.bukkit.inventory.ItemStack;

public abstract class ItemTagContext<T extends ItemTag> {

    public final void accept(AbstractItem tag, ItemStack itemStack){
        this.apply((T)tag, itemStack);
    }
    public abstract void apply(T item, ItemStack itemStack);

}
