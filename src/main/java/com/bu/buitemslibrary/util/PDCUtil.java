package com.bu.buitemslibrary.util;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PDCUtil {
    private static final Map<Class<?>, PersistentDataType<?, ?>> dataTypeRegistry = new HashMap<>();

    static{
        //Default PD types
        registerDataType(boolean.class, PersistentDataType.BOOLEAN);
        registerDataType(Boolean.class, PersistentDataType.BOOLEAN);
        registerDataType(byte.class, PersistentDataType.BYTE);
        registerDataType(Byte.class, PersistentDataType.BYTE);
        registerDataType(byte[].class, PersistentDataType.BYTE_ARRAY);
        registerDataType(double.class, PersistentDataType.DOUBLE);
        registerDataType(Double.class, PersistentDataType.DOUBLE);
        registerDataType(float.class, PersistentDataType.FLOAT);
        registerDataType(Float.class, PersistentDataType.FLOAT);
        registerDataType(int.class, PersistentDataType.INTEGER);
        registerDataType(Integer.class, PersistentDataType.INTEGER);
        registerDataType(int[].class, PersistentDataType.INTEGER_ARRAY);
        registerDataType(long.class, PersistentDataType.LONG);
        registerDataType(Long.class, PersistentDataType.LONG);
        registerDataType(long[].class, PersistentDataType.LONG_ARRAY);
        registerDataType(short.class, PersistentDataType.SHORT);
        registerDataType(Short.class, PersistentDataType.SHORT);
        registerDataType(String.class, PersistentDataType.STRING);
        registerDataType(PersistentDataContainer.class, PersistentDataType.TAG_CONTAINER);
        registerDataType(PersistentDataContainer[].class, PersistentDataType.TAG_CONTAINER_ARRAY);
    }

    public static <T> void registerDataType(Class<T> clazz, PersistentDataType<?, T> dataType){
        dataTypeRegistry.put(clazz, dataType);
    }

    public static <T> PersistentDataType<?, T> getDataType(Class<T> clazz){
        return (PersistentDataType<?, T>) dataTypeRegistry.get(clazz);
    }

    public static boolean canUseDataType(Class<?> clazz){
        return getDataType(clazz) != null;
    }

    public static String toCamelCase(String str){
        //THIS_IS_A_WORD -> thisIsAWord
        String[] words = str.split("_");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < words.length; i++){
            String word = words[i].toLowerCase();
            if(i != 0){
                word = word.substring(0, 1).toUpperCase() + word.substring(1);
            }
            sb.append(word);
        }
        return sb.toString();
    }

    public static String toTagCase(String str){
        //thisIsAWord -> THIS_IS_A_WORD
        String[] words = str.split("(?=\\p{Lu})");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < words.length; i++){
            String word = words[i].toUpperCase();
            if(i != words.length - 1){
                word += "_";
            }

            sb.append(word);

        }
        return sb.toString();
    }
}
