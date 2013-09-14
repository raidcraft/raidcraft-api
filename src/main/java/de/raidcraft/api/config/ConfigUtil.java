/*
 * CommandBook
 * Copyright (C) 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.raidcraft.api.config;

import de.raidcraft.api.config.typeconversions.BooleanTypeConversion;
import de.raidcraft.api.config.typeconversions.EnumTypeConversion;
import de.raidcraft.api.config.typeconversions.ListTypeConversion;
import de.raidcraft.api.config.typeconversions.MapTypeConversion;
import de.raidcraft.api.config.typeconversions.NumberTypeConversion;
import de.raidcraft.api.config.typeconversions.SameTypeConversion;
import de.raidcraft.api.config.typeconversions.SetTypeConversion;
import de.raidcraft.api.config.typeconversions.StringTypeConversion;
import de.raidcraft.api.config.typeconversions.TypeConversion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author zml2008
 */
public class ConfigUtil {

    private static final List<TypeConversion> typeConversions = new ArrayList<>(
            Arrays.asList(new SameTypeConversion(),
                    new StringTypeConversion(),
                    new BooleanTypeConversion(),
                    new NumberTypeConversion(),
                    new EnumTypeConversion(),
                    /*new ConfigurationBaseTypeConversion(),*/
                    new SetTypeConversion(),
                    new ListTypeConversion(),
                    new MapTypeConversion()
            ));

    public static Object smartCast(Type genericType, Object value) {

        if (value == null) {
            return null;
        }
        Type[] neededGenerics;
        Class target = null;
        if (genericType != null && genericType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) genericType;
            Type raw = type.getRawType();
            if (raw instanceof Class) {
                target = (Class) raw;
            }
            neededGenerics = type.getActualTypeArguments();
        } else {
            if (genericType instanceof Class) {
                target = (Class) genericType;
            }
            neededGenerics = new Type[0];
        }

        if (target == null) {
            return null;
        }

        Object ret = null;

        for (TypeConversion conversion : typeConversions) {
            if ((ret = conversion.handle(target, neededGenerics, value)) != null) {
                break;
            }
        }

        return ret;
    }

    public static void registerTypeConversion(TypeConversion conversion) {

        typeConversions.add(conversion);
    }

    @SuppressWarnings("unchecked")
    public static Object prepareSerialization(Object obj) {

        if (obj instanceof Collection) {
            obj = new ArrayList((Collection) obj);
        }
        return obj;
    }

    public static ConfigurationSection parseKeyValueTable(List<KeyValueMap> map) {

        ConfigurationSection configuration = new MemoryConfiguration();
        for (KeyValueMap entry : map) {
            try {
                configuration.set(entry.getDataKey(), Double.parseDouble(entry.getDataValue()));
            } catch (NumberFormatException e) {
                try {
                    configuration.set(entry.getDataKey(), Boolean.parseBoolean(entry.getDataValue()));
                } catch (NumberFormatException e1) {
                    configuration.set(entry.getDataKey(), entry.getDataValue());
                }
            }
        }
        return configuration;
    }
}
