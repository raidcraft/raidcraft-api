package de.raidcraft.api.items.tooltip;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Silthus
 */
public enum TooltipSlotType {

    NAME(NameTooltip.class),
    SINGLE(SingleLineTooltip.class),
    FIXED_MULTI_LINE(FixedMultilineTooltip.class),
    VARIABLE_MULTI_LINE(VariableMultilineTooltip.class),
    ATTRIBUTE(AttributeTooltip.class),
    DPS(DPSTooltip.class),
    EQUIPMENT_TYPE(EquipmentTypeTooltip.class),
    REQUIREMENT(RequirementTooltip.class),
    META_DATA(MetaDataTooltip.class);

    private final Class<? extends Tooltip> tooltip;

    TooltipSlotType(Class<? extends Tooltip> tooltip) {

        this.tooltip = tooltip;
    }

    public Tooltip create(Object... args) {

        try {
            Class<?>[] types = new Class[args.length];
            for (int i = 0; i < types.length; i++) {
                types[i] = args[i].getClass();
            }
            Constructor<? extends Tooltip> constructor = tooltip.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
