/*
 * Copyright or © or Copr. ZLib contributors (2015 - 2016)
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package fr.zcraft.zlib.components.configuration;

import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.items.ItemStackBuilder;
import fr.zcraft.zlib.tools.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class ConfigurationValueHandlers 
{
    static private final Map<Class, ValueHandler> valueHandlers = new HashMap<>();
    
    static {
        registerHandlers(ConfigurationValueHandlers.class);
    }
    
    private ConfigurationValueHandlers() {}
    
    static public void registerHandlers(Class handlersClass)
    {
        for(Method method : handlersClass.getMethods())
        {
            if(!Modifier.isStatic(method.getModifiers())) continue;
            ConfigurationValueHandler annotation = method.getAnnotation(ConfigurationValueHandler.class);
            if(annotation == null) continue;
            if(annotation.value().length == 0)
            {
                addHandler(method.getReturnType(), method);
            }
            else
            {
                for(Class klass : annotation.value())
                {
                    addHandler(klass, method);
                }
            }
        }
    }
    
    static private void addHandler(Class returnType, Method method)
    {
        ValueHandler handler = valueHandlers.get(returnType);
        
        if(handler == null)
        {
            handler = new ValueHandler(returnType);
            valueHandlers.put(returnType, handler);
        }
        
        Class[] parameterTypes = method.getParameterTypes();
        if(parameterTypes.length != 1)
            throw new IllegalArgumentException("Illegal value handler method '" + method.getName() + "' : method has to take one argument.");
        
        handler.addHandler(parameterTypes[0], method);
    }
    
    static public <T> T handleValue(Object obj, Class<T> outputType)  throws ConfigurationParseException
    {
        return handleValue(obj, outputType, null, null);
    }
    
    static public <T> T handleValue(Object obj, Class<T> outputType, ConfigurationItem parent, String tag) throws ConfigurationParseException
    {
        if(obj == null) return null;
        if(outputType == null) return (T) obj;//yolocast, strongly deprecated
        
        if(obj instanceof MemorySection)
        {
            obj = ((MemorySection) obj).getValues(false);
        }
        
        if(outputType.isAssignableFrom(obj.getClass())) return (T) obj;
        
        ValueHandler handler = valueHandlers.get(outputType);
        if(handler == null) 
        {
            if(Enum.class.isAssignableFrom(outputType))
            {
                return handleEnumValue(obj, outputType);
            }
            else if(ConfigurationSection.class.isAssignableFrom(outputType))
            {
                return (T) handleConfigurationItemValue(obj, outputType, parent, tag);
            }
            else
            {
                throw new UnsupportedOperationException("Unsupported configuration type : " + outputType.getName());
            }
        }
        
        try
        {
            return (T) handler.handleValue(obj);
        }
        catch (IllegalAccessException | IllegalArgumentException ex)
        {
            throw new RuntimeException("Unable to call handler for type " + outputType.getName(), ex);
        }
        catch (InvocationTargetException ex)
        {
            if(ex.getCause() instanceof ConfigurationParseException)
                throw (ConfigurationParseException) ex.getCause();
            
            throw new RuntimeException("Error while calling handler for type " + outputType.getName(), ex.getCause());
        }
    }
    
    /* ===== Value Handlers ===== */
    
    @ConfigurationValueHandler({Boolean.class, boolean.class})
    static public boolean handleBoolValue(Object obj) throws ConfigurationParseException 
    {
        return Boolean.parseBoolean(obj.toString());
    }
    
    @ConfigurationValueHandler({Byte.class, byte.class})
    static public byte handleByteValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Byte.parseByte(obj.toString(), 10);
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid byte value", obj);
        }
    }
    
    @ConfigurationValueHandler({Short.class, short.class})
    static public short handleShortValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Short.parseShort(obj.toString(), 10);
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid short value", obj);
        }
    }
    
    
    @ConfigurationValueHandler({Integer.class, int.class})
    static public int handleIntValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Integer.parseInt(obj.toString(), 10);
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid integer value", obj);
        }
    }
    
    @ConfigurationValueHandler({Long.class, long.class})
    static public long handleLongValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Long.parseLong(obj.toString(), 10);
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid long value", obj);
        }
    }
    
    @ConfigurationValueHandler({Float.class, float.class})
    static public float handleFloatValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Float.parseFloat(obj.toString());
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid float value", obj);
        }
    }
    
    @ConfigurationValueHandler({Double.class, double.class})
    static public double handleDoubleValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Double.parseDouble(obj.toString());
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid double value", obj);
        }
    }
    
    @ConfigurationValueHandler({Character.class, char.class})
    static public char handleCharValue(Object obj) throws ConfigurationParseException 
    {
        String str = obj.toString();
        if(str.length() > 1)
            throw new ConfigurationParseException("String is too long to fit in a single character", obj);
        return str.charAt(0);
    }
    
    @ConfigurationValueHandler
    static public String handleStringValue(Object obj) throws ConfigurationParseException 
    {
        return obj.toString();
    }
    
    @ConfigurationValueHandler
    static public Locale handleLocaleValue(Object obj) throws ConfigurationParseException 
    {
        if(obj.toString().isEmpty()) 
            return null;
        
        try
        {
            return new Locale.Builder().setLanguageTag(obj.toString()).build();
        }
        catch(IllformedLocaleException ex)
        {
            throw new ConfigurationParseException("Illegal language tag : " + ex.getMessage(), obj);
        }
    }
    
    static public <T> T handleEnumValue(Object obj, Class<T> enumClass) throws ConfigurationParseException
    {
        if(obj == null) return null;
        
        String strValue = obj.toString().toUpperCase().replace(' ', '_').replace('-', '_');
        
        try
        {
            return (T) Enum.valueOf((Class<Enum>) enumClass, strValue);
        }
        catch(IllegalArgumentException ex)
        {
            throw new ConfigurationParseException("Illegal enum value for type " + enumClass.getName(), obj);
        }
    }
    
    static public <T> ConfigurationSection handleConfigurationItemValue(Object obj, Class<T> sectionClass, ConfigurationItem parent, String tag) throws ConfigurationParseException
    {
        if(obj == null) return null;
        
        if(!(obj instanceof Map || obj instanceof MemorySection))
            throw new ConfigurationParseException("Dictionary expected", obj);
        
        if(parent == null || tag == null)
            throw new UnsupportedOperationException("ConfigurationSection values cannot be used here.");
        
        ConfigurationSection section;
        try
        {
            section = (ConfigurationSection) Reflection.instantiate(sectionClass);
            section.fieldName = tag;
            section.setParent(parent);
            section.init();
        }
        catch(Exception ex)
        {
            PluginLogger.warning("Unable to instanciate configuration field '{0}' of type '{1}'", ex, tag, sectionClass.getName());
            throw new RuntimeException(ex);
        }
        
        return section;
    }
    
    static public <T> List<T> handleListValue(Object value, Class<T> itemType) throws ConfigurationParseException
    {
        if(!(value instanceof List)) 
            throw new ConfigurationParseException("List expected", value);
        
        List rawList = (List) value;
        ArrayList<T> newList = new ArrayList<>(rawList.size());
        for(Object val : rawList)
        {
            if(val == null) continue;
            newList.add(handleValue(val, itemType, null, null));
        }
        
        return newList;
    }
    
    static public <K,V> Map<K,V> handleMapValue(Object value, Class<K> keyType, Class<V> valueType) throws ConfigurationParseException
    {
        return handleMapValue(value, keyType, valueType, null);
    }
    
    static public <K,V> Map<K,V> handleMapValue(Object value, Class<K> keyType, Class<V> valueType, ConfigurationItem parent) throws ConfigurationParseException
    {
        Map<String, Object> rawMap;
        
        if(value instanceof Map)
        {
            rawMap = (Map) value;
        }
        else if(value instanceof MemorySection)
        {
            rawMap = ((MemorySection) value).getValues(false);
        }
        else
        {
            throw new ConfigurationParseException("Dictionary expected", value);
        }
        
        HashMap<K,V> newMap = new HashMap<>();
        for(Map.Entry entry : rawMap.entrySet())
        {
            if(entry == null) continue;
            if(entry.getKey() == null || entry.getValue() == null) continue;
            newMap.put(ConfigurationValueHandlers.handleValue(entry.getKey(), keyType, null, null),
                    ConfigurationValueHandlers.handleValue(entry.getValue(), valueType, parent, entry.getKey().toString()));
        }
        
        return newMap;
    }
    
    @ConfigurationValueHandler
    static public Vector handleBukkitVectorValue(String str) throws ConfigurationParseException
    {
        return handleBukkitVectorValue(Arrays.asList(str.split(",")));
    }
    
    @ConfigurationValueHandler
    static public Vector handleBukkitVectorValue(List list) throws ConfigurationParseException 
    {
        if(list.size() < 2)
            throw new ConfigurationParseException("Not enough values, at least 2 (x,z) are required.", list);
        if(list.size() > 3)
            throw new ConfigurationParseException("Too many values, at most 3 (x,y,z) can be used.", list);
        
        if(list.size() == 2)
        {
            return new Vector(handleDoubleValue(list.get(0)), 0, handleDoubleValue(list.get(1)));
        }
        else
        {
            return new Vector(handleDoubleValue(list.get(0)), handleDoubleValue(list.get(1)), handleDoubleValue(list.get(2)));
        }
    }

    @ConfigurationValueHandler
    static public World handleBukkitWorldValue(String str) throws ConfigurationParseException
    {
        String worldName = str.trim();
        for (World world : Bukkit.getWorlds())
            if (world.getName().equalsIgnoreCase(worldName))
                return world;

        throw new ConfigurationParseException("World not found", str);
    }
    
    @ConfigurationValueHandler
    static public Vector handleBukkitVectorValue(Map map) throws ConfigurationParseException
    {
        double x = map.containsKey("x") ? handleDoubleValue(map.get("x")) : 0;
        double y = map.containsKey("y") ? handleDoubleValue(map.get("y")) : 0;
        double z = map.containsKey("z") ? handleDoubleValue(map.get("z")) : 0;
        
        return new Vector(x, y, z);
    }
    
    @ConfigurationValueHandler
    static public Enchantment handleEnchantmentValue(String value) throws ConfigurationParseException
    {
        Enchantment enchantment = Enchantment.getByName(value.toUpperCase());
        if(enchantment == null) throw new ConfigurationParseException("Invalid enchantment name", value);
        return enchantment;
    }
    
    @ConfigurationValueHandler
    static public ItemStack handleItemStackValue(Map map) throws ConfigurationParseException
    {
        if(!map.containsKey("type"))
            throw new ConfigurationParseException("Key 'type' required.", map);
        
        Material material = handleEnumValue(map.get("type"), Material.class);
        int amount = map.containsKey("amount") ? handleIntValue(map.get("amount")) : 1;
        
        ItemStackBuilder item;
        
        if(material.equals(Material.POTION))
        {
            Potion potion = handlePotionValue(map);
            item = new ItemStackBuilder(potion.toItemStack(amount));
        }
        else
        {
            item = new ItemStackBuilder(material, amount);
        }
        
        if(map.containsKey("data"))
            item.data(handleShortValue(map.get("data")));
        
        if(map.containsKey("title"))
            item.title(map.get("title").toString());
        
        if(map.containsKey("lore"))
            item.lore(handleListValue(map.get("lore"), String.class));
        
        if(map.containsKey("glow"))
            item.glow(handleBoolValue(map.get("glow")));
        
        if(map.containsKey("hideAttributes"))
            if(handleBoolValue(map.get("hideAttributes")))
                item.hideAttributes();
        
        if(map.containsKey("enchantments"))
            item.enchant(handleMapValue(map.get("enchantments"), Enchantment.class, Integer.class));
        
        return item.item();
    }
    
    @ConfigurationValueHandler
    static public Potion handlePotionValue(Map map) throws ConfigurationParseException
    {
        if(!map.containsKey("effect"))
            throw new ConfigurationParseException("Potion effect is required.", map);
        
        PotionType type = handleEnumValue(map.get("effect"), PotionType.class);
        int level = map.containsKey("level") ? handleByteValue(map.get("level")) : 1;
        boolean splash = map.containsKey("splash") ? handleBoolValue(map.get("splash")) : false;
        boolean extended = map.containsKey("extended") ? handleBoolValue(map.get("extended")) : false;
        
        return new Potion(type, level, splash, extended);
    }
    
    @ConfigurationValueHandler
    static public DyeColor handleDyeColorValue(Integer value) throws ConfigurationParseException
    {
        DyeColor color = DyeColor.getByDyeData((byte) (int) value);
        
        if(color == null)
            throw new ConfigurationParseException("Invalid dye color code", value);
        
        return color;
    }
    
    static private Object getRawValue(Map map, String key)
    {
        Object value = null;
        for(Object mapKey : map.keySet())
        {
            if(mapKey.toString().equalsIgnoreCase(key))
                value = map.get(mapKey);
        }
        
        return value;
    }
    
    static public <T> T getValue(Map map, String key, T defaultValue) throws ConfigurationParseException
    {
        return getValue(map, key, defaultValue, (Class<T>) defaultValue.getClass());
    }
    
    static public <T> T getValue(Map map, String key, T defaultValue, Class<T> valueType) throws ConfigurationParseException
    {
        Object rawValue = getRawValue(map, key);
        if(rawValue == null) return defaultValue;
        return handleValue(rawValue, valueType);
    }
    
    static public <T> List<T> getListValue(Map map, String key, List<T> defaultValue, Class<T> valueType) throws ConfigurationParseException
    {
        Object rawValue = getRawValue(map, key);
        if(rawValue == null) return defaultValue;
        return handleListValue(rawValue, valueType);
    }
}
