package de.raidcraft.api.config.typeconversions;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zml2008
 */
public class ListTypeConversion extends TypeConversion {

    @Override
    protected Object cast(Class<?> target, Type[] neededGenerics, Object value) {

        List<Object> values = new ArrayList<>();
        Collection raw = (Collection) value;
        for (Object obj : raw) {
            values.add(de.raidcraft.util.ConfigUtil.smartCast(neededGenerics[0], obj));
        }
        return values;
    }

    @Override
    public boolean isApplicable(Class<?> target, Object value) {

        return target.equals(List.class) && value instanceof Collection;
    }

    @Override
    public int getParametersRequired() {

        return 1;
    }
}
