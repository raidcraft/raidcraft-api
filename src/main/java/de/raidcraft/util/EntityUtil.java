package de.raidcraft.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import java.util.*;
import java.lang.reflect.*;

/**
 * @author Silthus
 */
public class EntityUtil {

    private static final char HEALTH_BAR_OUTTER_LEFT = '╣';
    private static final char HEALTH_BAR_OUTTER_RIGHT = '╠';
    private static final char ELITE_SYMBOL = '†';
    private static final char RARE_SYMBOL = '♣';
    private static final char HEALTH_BAR_MAIN_SYMBOL = '█';
    private static final char HEALTH_BAR_HALF_SYMBOL = '▌';
    private static final int HEALTH_BAR_LENGTH = 10;

    public static void addPanicMode(LivingEntity entity) {

        /*if (entity instanceof Creature) {
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
        }*/
    }

    public static void walkToLocation(LivingEntity entity, Location loc, float speed) {

        /*((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), speed);*/
    }

    public static String drawMobName(String name, int level, ChatColor color, boolean elite, boolean rare) {

        StringBuilder sb = new StringBuilder();

        if (elite) sb.append(ChatColor.DARK_RED).append(ELITE_SYMBOL);
        if (rare) sb.append(ChatColor.BLUE).append(RARE_SYMBOL);

        if (elite || rare) sb.append(" ");
        sb.append(ChatColor.DARK_PURPLE).append("[").append(color).append(level).append(ChatColor.DARK_PURPLE).append("] ");
        sb.append(color).append(name);
        if (elite || rare) sb.append(" ");

        if (rare)  sb.append(ChatColor.BLUE).append(RARE_SYMBOL);
        if (elite) sb.append(ChatColor.DARK_RED).append(ELITE_SYMBOL);

        return sb.toString();
    }

    public static String drawMobName(String name, int mobLevel, int playerLevel, boolean elite, boolean rare) {

        return drawMobName(name, mobLevel, getConColor(playerLevel, mobLevel), elite, rare);
    }

    public static String drawHealthBar(double health, double maxHealth, ChatColor mobColor) {

        return drawHealthBar(health, maxHealth, mobColor, false, false);
    }

    public static String drawHealthBar(double health, double maxHealth, ChatColor mobColor, boolean elite, boolean rare) {

        ChatColor barColor = ChatColor.GREEN;
        double healthInPercent = health / maxHealth;
        if (healthInPercent < 0.20) {
            barColor = ChatColor.DARK_RED;
        } else if (healthInPercent < 0.35) {
            barColor = ChatColor.RED;
        } else if (healthInPercent < 0.50) {
            barColor = ChatColor.GOLD;
        } else if (healthInPercent < 0.75) {
            barColor = ChatColor.YELLOW;
        } else if (healthInPercent < 0.90) {
            barColor = ChatColor.DARK_GREEN;
        }

        StringBuilder healthBar = new StringBuilder();

        if (elite) healthBar.append(ChatColor.DARK_RED).append(ELITE_SYMBOL);
        if (rare) healthBar.append(ChatColor.BLUE).append(RARE_SYMBOL);
        if (elite || rare) healthBar.append(" ");

        healthBar.append(mobColor).append(HEALTH_BAR_OUTTER_LEFT);
        healthBar.append(barColor);

        int count = (int) (healthInPercent * HEALTH_BAR_LENGTH);
        double modulo = (healthInPercent * (HEALTH_BAR_LENGTH * 10)) % 10;
        boolean appendHalfBar = modulo < 6 && modulo > 0;
        for (int i = 0; i < count; i++) {
            if (i == count - 1 && appendHalfBar) {
                break;
            }
            healthBar.append(HEALTH_BAR_MAIN_SYMBOL);
        }
        if (appendHalfBar) {
            healthBar.append(HEALTH_BAR_HALF_SYMBOL);
        }
        for (int i = 0; i < HEALTH_BAR_LENGTH - count; i++) {
            healthBar.append("  ");
        }
        // and append the ending
        healthBar.append(mobColor).append(HEALTH_BAR_OUTTER_RIGHT);

        if (elite || rare) healthBar.append(" ");
        if (rare)  healthBar.append(ChatColor.BLUE).append(RARE_SYMBOL);
        if (elite) healthBar.append(ChatColor.DARK_RED).append(ELITE_SYMBOL);

        return healthBar.toString();
    }

    /**
     * Mob XP Functions (including Con Colors)
     * Colors will be numbers:
     * {grey = 0, green = 1, yellow = 2, orange = 3, red = 4, skull = 5}
     * NOTE: skull = red when working with anything OTHER than mobs!
     * @param playerlvl the level of the player
     * @param moblvl the level of the mob
     * @return difficulty color
     */
    public static ChatColor getConColor(int playerlvl, int moblvl) {

        if (playerlvl + 5 <= moblvl) {
            if (playerlvl + 10 <= moblvl) {
                return ChatColor.DARK_RED;
            } else {
                return ChatColor.RED;
            }
        } else {
            switch (moblvl - playerlvl) {
                case 4:
                case 3:
                    return ChatColor.GOLD;
                case 2:
                case 1:
                case 0:
                case -1:
                case -2:
                    return ChatColor.YELLOW;
                default:
                    // More adv formula for grey/green lvls:
                    if (playerlvl <= 5) {
                        return ChatColor.GREEN; //All others are green.
                    } else {
                        if (playerlvl <= 39) {
                            if (moblvl <= (playerlvl - 5 - Math.floor(playerlvl / 10))) {
                                // Its below or equal to the 'grey level':
                                return ChatColor.GRAY;
                            } else {
                                return ChatColor.GREEN;
                            }
                        } else {
                            //player over lvl 39:
                            if (moblvl <= (playerlvl - 1 - Math.floor(playerlvl / 5))) {
                                return ChatColor.GRAY;
                            } else {
                                return ChatColor.GREEN;
                            }
                        }
                    }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void registerEntity(String name, int id, Class<?> customClass) {
        try {
         
            // we need to check if the class is a valid transistent entity
            // and because we dont want to break every version we are using reflection
            if (!ReflectionUtil.getNmsClass("net.minecraft.server", "EntityInsentient").isAssignableFrom(customClass)) {
                return;
            }
            List<Map<?, ?>> dataMaps = new ArrayList<Map<?, ?>>();
            for (Field f : ReflectionUtil.getNmsClass("net.minecraft.server", "EntityTypes").getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMaps.add((Map<?, ?>) f.get(null));
                }
            }
            
            ((Map<Class<?>, String>) dataMaps.get(1)).put(customClass, name);
            ((Map<Class<?>, Integer>) dataMaps.get(3)).put(customClass, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}