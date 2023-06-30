package com.bu.buitemslibrary;

import com.bu.buitemslibrary.eventhandler.ListenerRegistry;
import com.bu.buitemslibrary.itemtag.CustomModelData;
import com.bu.buitemslibrary.itemtag.ItemTag;
import com.bu.buitemslibrary.itemtag.ItemTagContext;
import com.bu.buitemslibrary.itemtag.NotStackable;
import org.bukkit.NamespacedKey;
import org.bukkit.event.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BuItemManager implements Listener {
    private final JavaPlugin plugin;
    private final ListenerRegistry listenerRegistry;

    private final Map<Class<? extends AbstractItem>, AbstractItem> itemRegistry = new HashMap<>();
    private final Map<Class<? extends ItemTag>, ItemTagContext<? extends ItemTag>> itemTagRegistry = new HashMap<>();

    public BuItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.listenerRegistry = new ListenerRegistry(plugin);

        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);


        //Apply default tags
        registerDefaultItemTags();
    }

    private void registerDefaultItemTags(){
        registerItemTag(CustomModelData.class, new ItemTagContext<CustomModelData>() {
            @Override
            public void apply(CustomModelData tag, ItemStack itemStack) {
                ItemMeta meta = itemStack.getItemMeta();
                int customModelData = tag.customModelData();
                meta.setCustomModelData(customModelData);
                itemStack.setItemMeta(meta);
            }
        });

        registerItemTag(NotStackable.class, new ItemTagContext<NotStackable>() {
            @Override
            public void apply(NotStackable item, ItemStack itemStack) {
                ItemMeta meta = itemStack.getItemMeta();
                PersistentDataContainer pdc = meta.getPersistentDataContainer();

                //Apply random uuid so items can't stack
                pdc.set(new NamespacedKey(plugin, "STACKABLE_ID"), PersistentDataType.STRING, UUID.randomUUID().toString());
                itemStack.setItemMeta(meta);
            }
        });
    }

    //Handler Registration
    /**
    * Register new item handler.
    * @param handlers
    */
    public void registerHandler(AbstractItem... handlers){
        for(AbstractItem handler : handlers){
            this.registerHandler(handler);
        }
    }

    /**
     * Register new item handler.
     * @param handler
     */
    public void registerHandler(AbstractItem handler){
        Class<? extends AbstractItem> clazz = handler.getClass();
        itemRegistry.put(clazz, handler);
        handler.initialise(plugin, this);
        plugin.getServer().getPluginManager().registerEvents(handler, plugin);

        //Use in house event handler to cache methods
        //listenerRegistry.registerListener(handler);
    }

    /**
     * Register new item by AbstractItem class.
     * <br>
     * Will throw RuntimeException if class does not have a no-argument constructor
     * <br>
     * WARNING: Uses Reflection.
     * @param classes
     */
    @SafeVarargs
    public final void registerHandler(Class<? extends AbstractItem>... classes){
        for(Class<? extends AbstractItem> clazz : classes){
            this.registerHandler(clazz);
        }
    }

    /**
     * Register new item by AbstractItem class.
     * <br>
     * Will throw RuntimeException if class does not have a no-argument constructor
     * <br>
     * WARNING: Uses Reflection.
     * @param clazz
     */
    public void registerHandler(Class<? extends AbstractItem> clazz){
        try {
            AbstractItem item = clazz.getDeclaredConstructor().newInstance();
            registerHandler(item);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns registered handler by handler class.
     * <br>
     * Will return null if not present in registry.
     * @param clazz
     * @return
     * @param <T>
     */
    public <T extends AbstractItem> T getHandler(Class<? extends T> clazz){
        return (T) itemRegistry.get(clazz);
    }

    /**
     * Returns registered handler by itemId.
     * <br>
     * Will return null if not present in registry.
     * @param itemId
     * @return
     */
    public AbstractItem getHandler(String itemId){
        for(AbstractItem handler : itemRegistry.values()){
            if(handler.getItemID().equals(itemId))
                return handler;
        }
        return null;
    }

    /**
     * Returns all <u>registered</u> item handlers.
     * @return
     */
    public Map<Class<? extends AbstractItem>, AbstractItem> getAllHandlers(){
        return this.itemRegistry;
    }

    //Item Tag Registration

    /**
     * Register new item tag class. Takes consumer which is called on each item generation for applicable item handlers.
     * @param itemTag
     * @param consumer
     * @param <T>
     */
    public <T extends ItemTag> void registerItemTag(Class<? extends T> itemTag, ItemTagContext<T> consumer){
        itemTagRegistry.put(itemTag, consumer);
    }

    /**
     * Returns entire item tag registry.
     * @return
     */
    public Map<Class<? extends ItemTag>, ItemTagContext<? extends ItemTag>> getItemTagRegistry(){
        return this.itemTagRegistry;
    }

    /**
     * Returns all <u>registered</u> item tags.
     * @return
     */
    public Set<Class<? extends ItemTag>> getAllItemTags(){
        return this.itemTagRegistry.keySet();
    }

}
























