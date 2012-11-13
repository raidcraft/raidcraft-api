package de.raidcraft.api.player;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import net.milkbowl.vault.economy.Economy;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractPlayer implements RCPlayer {

	private final Map<Class<? extends PlayerComponent>, PlayerComponent> components = new HashMap<>();

	private final String username;
	private String displayName;

	protected AbstractPlayer(String username) {

		this.username = username;
		this.displayName = username;
	}

	@Override
	public final <T extends PlayerComponent> T getComponent(Class<T> clazz) {

		PlayerComponent component = null;
		if (components.containsKey(clazz)) {
			component = components.get(clazz);
		} else {
			try {
				// lets check if the clazz has a constructor taking RCPlayer as argument
				Constructor<T> constructor = clazz.getConstructor(RCPlayer.class);
				// lets set it accessible if private
				constructor.setAccessible(true);
				// create the instance
				component = constructor.newInstance(this);
				// and attach it to our RCPlayer object cache
				components.put(clazz, component);
			} catch (ReflectiveOperationException e) {
				RaidCraft.LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		}
		return clazz.cast(component);
	}

	@Override
	public String getUserName() {
		return username;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public boolean isInWorld(String world) {
		return world.equalsIgnoreCase(getWorld());
	}

    @Override
    public boolean hasEnoughMoney(double cost) {

        Economy economy = RaidCraft.getComponent(RaidCraftPlugin.class).getEconomy();
        if (economy == null) return false;
        return economy.has(getUserName(), cost);
    }

    @Override
    public void addMoney(double amount) {

        Economy economy = RaidCraft.getComponent(RaidCraftPlugin.class).getEconomy();
        if (economy == null) return;
        economy.depositPlayer(getUserName(), amount);
    }

    @Override
    public void removeMoney(double amount) {

        Economy economy = RaidCraft.getComponent(RaidCraftPlugin.class).getEconomy();
        if (economy == null) return;
        economy.withdrawPlayer(getUserName(), amount);
    }

	@Override
	public String toString() {

		return username;
	}
}
