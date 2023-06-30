package com.bu.buitemslibrary.itemstats;

import com.bu.buitemslibrary.util.PDCUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * WARNING: Uses Reflection.
 */
public abstract class ItemStatInstance {

    protected boolean mutable = true;
    private JavaPlugin plugin;
    private ItemStack itemStack;

    public ItemStatInstance(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void load(ItemStack itemStack){
        if(itemStack == null) return;
        this.itemStack = itemStack;
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        for(Field field : this.getClass().getDeclaredFields()){
            try {
                Class<?> clazz = field.getType();
                if (Modifier.isTransient(field.getModifiers())) continue;
                String name = field.getName();
                if (!PDCUtil.canUseDataType(clazz)) {
                    throw new RuntimeException("Attempted to serialize field '" + name
                            + "'(type " + clazz.getSimpleName() + ") in class '" + this.getClass().getSimpleName() + "'. Add the PersistentDataType adapter to the PDCUtil registry to serialize.");
                }
                PersistentDataType<?, ?> pdt = PDCUtil.getDataType(clazz);
                Object val = pdc.get(new NamespacedKey(plugin, PDCUtil.toTagCase(name)), pdt);
                field.set(this, val);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
                //Ignore Illegal Argument as its most likely trying to set numerical to null. Will set to default of instance class
            } catch (IllegalArgumentException ignored){}
        }

    }

    public void finish(){
        if(!mutable){
            throw new RuntimeException("Item Stat Instance already closed. Consider creating a new instance to mutate variables.");
        }
        mutable = false;

        if(itemStack == null) return;
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        for(Field field : this.getClass().getDeclaredFields()){
            try {
                Class<?> clazz = field.getType();
                if (Modifier.isTransient(field.getModifiers())) continue;
                String name = field.getName();
                if (!PDCUtil.canUseDataType(clazz)) {
                    throw new RuntimeException("Attempted to serialize field '" + name
                            + "'(type " + clazz.getSimpleName() + ") in class '" + this.getClass().getSimpleName() + "'. Add the PersistentDataType adapter to the PDCUtil registry to serialize.");
                }
                PersistentDataType<?, ?> pdt = PDCUtil.getDataType(clazz);
                Object val = field.get(this);
                setPDCAssertType(pdc, new NamespacedKey(plugin, PDCUtil.toTagCase(name)), pdt, val);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        itemStack.setItemMeta(meta);
    }

    //Absolute Type erasure trickery - Do not gaze upon this code for too long
    private static <E> void setPDCAssertType(PersistentDataContainer pdc, NamespacedKey key, PersistentDataType<?, E> pdt, Object val) {
        pdc.set(key, pdt, (E)val);
    }
}
