package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class EffectUtil {

    private static final EffectUtil instance = new EffectUtil();

    public static void playSound(Location location, Sound sound, float volume, float pitch) {

        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public static void playEffect(Location location, Effect effect, int data) {

        location.getWorld().playEffect(location, effect, data);
    }

    public static void playFirework(World world, Location location, FireworkEffect effect) {

        try {
            instance.playFireworks(world, location, effect);
        } catch (Exception e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Location> circle(Location loc, Integer radius, Integer height, Boolean hollow, Boolean sphere, int plus_y) {

        List<Location> circleblocks = new ArrayList<>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - radius; x <= cx +radius; x++)
            for (int z = cz - radius; z <= cz +radius; z++)
                for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < radius*radius && !(hollow && dist < (radius-1)*(radius-1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }

        return circleblocks;
    }

    /*
	 * Example use:
	 *
	 * public class FireWorkPlugin implements Listener {
	 *
	 * FireworkEffectPlayer fplayer = new FireworkEffectPlayer();
	 *
	 * @EventHandler
	 * public void onPlayerLogin(PlayerLoginEvent event) {
	 *   fplayer.playFireworks(event.getPlayer().getWorld(), event.getPlayer.getLocation(), Util.getRandomFireworkEffect());
	 * }
	 *
	 * }
	 */

    // internal references, performance improvements
    private Method world_getHandle = null;
    private Method nms_world_broadcastEntityEffect = null;
    private Method firework_getHandle = null;

    /**
     * Play a pretty firework at the location with the FireworkEffect when called
     * @param world
     * @param loc
     * @param fe
     * @throws Exception
     */
    public void playFireworks(World world, Location loc, FireworkEffect fe) throws Exception {
        // Bukkity load (CraftFirework)
        Firework fw = world.spawn(loc, Firework.class);
        // the net.minecraft.server.World
        Object nms_world;
        Object nms_firework;
		/*
		 * The reflection part, this gives us access to funky ways of messing around with things
		 */
        if(world_getHandle == null) {
            // get the methods of the craftbukkit objects
            world_getHandle = getMethod(world.getClass(), "getHandle");
            firework_getHandle = getMethod(fw.getClass(), "getHandle");
        }
        // invoke with no arguments
        nms_world = world_getHandle.invoke(world, (Object[]) null);
        nms_firework = firework_getHandle.invoke(fw, (Object[]) null);
        // null checks are fast, so having this seperate is ok
        if(nms_world_broadcastEntityEffect == null) {
            // get the method of the nms_world
            nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(), "broadcastEntityEffect");
        }
		/*
		 * Now we mess with the metadata, allowing nice clean spawning of a pretty firework (look, pretty lights!)
		 */
        // metadata load
        FireworkMeta data = fw.getFireworkMeta();
        // clear existing
        data.clearEffects();
        // power of one
        data.setPower(1);
        // add the effect
        data.addEffect(fe);
        // set the meta
        fw.setFireworkMeta(data);
        /*
		 * Finally, we broadcast the entity effect then kill our fireworks object
		 */
        // invoke with arguments
        nms_world_broadcastEntityEffect.invoke(nms_world, nms_firework, (byte) 17);
        // remove from the game
        fw.remove();
    }

    /**
     * Internal method, used as shorthand to grab our method in a nice friendly manner
     * @param cl
     * @param method
     * @return Method (or null)
     */
    private static Method getMethod(Class<?> cl, String method) {
        for(Method m : cl.getMethods()) {
            if(m.getName().equals(method)) {
                return m;
            }
        }
        return null;
    }
}
