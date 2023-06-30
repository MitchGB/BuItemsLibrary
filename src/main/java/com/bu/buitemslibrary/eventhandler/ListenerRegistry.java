package com.bu.buitemslibrary.eventhandler;

import com.bu.buitemslibrary.util.ReflectionUtil;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

//IN-HOUSE EVENT HANDLER
//I've added this to make event handling seamless without wrapper overhead, at the same time avoiding reflection
//Anything here should *not* be registered with plugin manager
public class ListenerRegistry implements Listener{
    private final JavaPlugin plugin;
    private final Set<Class<? extends Event>> eventClassCache = new HashSet<>();
    private final Map<Class<? extends Listener>, Map<Class<? extends Event>, Consumer<? extends Event>>> listenerMethodRegistry = new HashMap<>();

    public ListenerRegistry(JavaPlugin plugin) {
        this.plugin = plugin;

        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> eventHandler(event), EventPriority.HIGHEST, plugin, true);
        for (HandlerList handler : HandlerList.getHandlerLists()) {
            handler.register(registeredListener);
        }
    }

    public void registerListener(Listener... listeners){
        for(Listener listener : listeners){
            this.registerListener(listener);
        }
    }

    public void registerListener(Listener listener) {
        Map<Class<? extends Event>, Consumer<? extends Event>> localMap = new HashMap<>();

        try {
            Method[] methods = listener.getClass().getDeclaredMethods();
            for(Method method : methods){
                if(!method.isAnnotationPresent(EventHandler.class)) continue;
                if(method.getParameterCount() != 1) continue;
                Parameter param = method.getParameters()[0];
                if(!Event.class.isAssignableFrom(param.getType())) continue;

                //I love blind casting nah fr tho this is safe
                Class<? extends Event> clazz = (Class<? extends Event>) param.getType();

                //convert to consumer
                Consumer<? extends Event> consumer = ReflectionUtil.toConsumer(clazz, MethodHandles.lookup(), listener, method);
                eventClassCache.add(clazz);
                System.out.println(clazz.getSimpleName());
                localMap.put(clazz, consumer);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        listenerMethodRegistry.put(listener.getClass(), localMap);
    }

    public void eventHandler(Event event){
        Class<? extends Event> clazz = event.getClass();
        //if cache doesn't contain class, skip
        if(clazz == EntityAirChangeEvent.class) return;
        //System.out.println(clazz.getSimpleName());

        if(!eventClassCache.contains(clazz)) return;

        long time = System.nanoTime();
        //System.out.println(clazz.getSimpleName());
        //System.out.println(System.nanoTime() - time);
    }
}
