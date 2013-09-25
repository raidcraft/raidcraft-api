package de.raidcraft.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class EntityUtil {

    private static final char HEALTH_BAR_OUTTER_LEFT = '╣';
    private static final char HEALTH_BAR_OUTTER_RIGHT = '╠';
    private static final char HEALTH_BAR_FILLER = '|';
    private static final char HEALTH_BAR_MAIN_SYMBOL = '█';
    private static final char HEALTH_BAR_HALF_SYMBOL = '▌';
    private static final int HEALTH_BAR_LENGTH = 30;

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

    public static String drawHealthBar(double health, double maxHealth, ChatColor mobColor) {

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
        // lets start out with an always green left part
        healthBar.append(ChatColor.BOLD).append(mobColor).append(HEALTH_BAR_OUTTER_LEFT);
        int count = (int) (healthInPercent * HEALTH_BAR_LENGTH);
        healthBar.append(barColor);
        for (int i = 0; i < HEALTH_BAR_LENGTH; i++) {
            if (i == count) {
                healthBar.append(ChatColor.BLACK);
            }
            healthBar.append(HEALTH_BAR_FILLER);
        }
        // and append the ending
        healthBar.append(mobColor).append(HEALTH_BAR_OUTTER_RIGHT);

        return healthBar.toString();
    }

/*    public static String drawHealthBar(double health, double maxHealth) {

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

        StringBuilder healthBar = new StringBuilder(ChatColor.BLACK + "[" + barColor + health + ChatColor.BLACK
                + "/" + ChatColor.GREEN + maxHealth + ChatColor.BLACK + "]").append(barColor);

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
        return healthBar.toString();
    }*/
}
