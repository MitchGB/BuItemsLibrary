package com.bu.buitemslibrary;

import com.bu.buitemslibrary.itemtag.ItemTag;
import com.bu.buitemslibrary.itemtag.ItemTagContext;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public abstract class AbstractItem implements Listener {

    protected BuItemManager itemManager;
    protected JavaPlugin plugin;
    public final String itemID;
    public final Material material;

    public AbstractItem(String itemID, Material material) {
        this.itemID = itemID;
        this.material = material;
    }

    /**
     * INTERNAL USE - DO NOT USE
     * @param itemManager
     */
    public final void initialise(JavaPlugin plugin, BuItemManager itemManager){
        //Insert plugin and itemManager instance. Makes it easy, so we don't have to pass it in constructor :)
        this.plugin = plugin;
        this.itemManager = itemManager;
    }

    /**
     * INTERNAL USE - DO NOT USE
     */
    private boolean isInitialised(){
        return this.itemManager.getHandler(this.getClass()) != null;
    }

    /**
     * Returns whether the given ItemStack is applicable to this item handler
     * @param itemStack
     * @return boolean - whether the ItemStack is applicable
     */
    public boolean isApplicable(ItemStack itemStack){
        if(!isInitialised())
            throw new RuntimeException("Item handler " + this.getClass().getSimpleName() + " is not registered. Please use itemManager#registerHandler before using any internal methods.");

        if(itemStack == null)
            return false;

        if(!itemStack.hasItemMeta())
            return false;

        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String pdcId = pdc.get(new NamespacedKey(plugin, "ITEM_ID"), PersistentDataType.STRING);
        return itemID.equals(pdcId);
    }

    /**
     * Get ItemStack for player use
     * @param player
     * @return
     */
    public ItemStack getItem(Player player){
        if(!isInitialised())
            throw new RuntimeException("Item handler " + this.getClass().getSimpleName() + " is not registered. Please use itemManager#registerHandler before using any internal methods.");

        ItemStack itemStack = new ItemStack(material);

        //Apply Item ID pdc
        applyItemID(itemStack);
        //Apply tags
        applyItemTags(itemStack);



        return generateItem(itemStack, player);
    }

    private void applyItemID(ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, "ITEM_ID"), PersistentDataType.STRING, itemID);
        itemStack.setItemMeta(meta);

    }

    private void applyItemTags(ItemStack itemStack){
        for(Map.Entry<Class<? extends ItemTag>, ItemTagContext<? extends ItemTag>> entry : itemManager.getItemTagRegistry().entrySet()){
            Class<? extends ItemTag> clazz = entry.getKey();
            if(!clazz.isAssignableFrom(this.getClass())) continue;

            ItemTagContext<? extends ItemTag> consumer = entry.getValue();
            if(consumer == null) continue;
            consumer.accept(this, itemStack);
        }
    }

    /**
     * Internal implementation of getItem
     * <br>
     * Override this and implement item modifications.
     * @param itemStack
     * @param player
     * @return
     */
    protected abstract ItemStack generateItem(ItemStack itemStack, Player player);

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof AbstractItem)) return false;
        AbstractItem absItem = (AbstractItem) obj;
        return absItem.itemID.equals(this.itemID);
    }

    @Override
    public int hashCode(){
        return this.itemID.hashCode();
    }
}





































