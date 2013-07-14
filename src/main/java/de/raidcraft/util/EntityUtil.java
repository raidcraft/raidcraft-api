package de.raidcraft.util;

import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class EntityUtil {

     public static void addPanicMode(LivingEntity entity) {

         /*
        if (entity instanceof Creature) {
            try {
                EntityCreature handle = ((CraftCreature) entity).getHandle();
                PathfinderGoalPanic goal = new PathfinderGoalPanic(handle, 0.38F);
                Field field = EntityLiving.class.getDeclaredField("goalSelector");
                field.setAccessible(true);
                PathfinderGoalSelector selector = (PathfinderGoalSelector) field.get(handle);
                selector.a(1, goal);
                field.set(handle, selector);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        */
    }
}
