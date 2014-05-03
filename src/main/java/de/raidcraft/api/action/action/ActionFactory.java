package de.raidcraft.api.action.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class ActionFactory implements Component {

    private static final ActionFactory INSTANCE = new ActionFactory();
    @NonNull
    public static ActionFactory getInstance() {

        return INSTANCE;
    }

    private final Map<String, Action<?>> actions = new CaseInsensitiveMap<>();

    private ActionFactory() {

        RaidCraft.registerComponent(ActionFactory.class, this);
        registerGlobalActions();
    }

    private void registerGlobalActions() {

        registerAction("player.give.item", new Action<Player>() {
            @Override
            public void accept(Player player) {

                try {
                    ItemStack item = RaidCraft.getItem(getConfig().getString("item"), getConfig().getInt("amount"));
                    player.getInventory().addItem(item);
                } catch (CustomItemException e) {
                    RaidCraft.LOGGER.warning("player.give.item (" + player.getName() + "): " + e.getMessage());
                }
            }
        });
        registerAction("player.kill", (Player player) -> player.setHealth(0.0));
    }

    private <T> void registerAction(@NonNull String identifier, @NonNull Action<T> action) {

        actions.put(identifier, action);
        RaidCraft.LOGGER.info("registered global action: " + identifier);
    }

    @SneakyThrows
    public <T> void registerAction(@NonNull JavaPlugin plugin, @NonNull String identifier, @NonNull Action<T> action) {

        identifier = plugin.getName() + "." + identifier;
        if (actions.containsKey(identifier)) {
            throw new ActionException("Action '" + identifier + "' is already registered!");
        }
        actions.put(identifier, action);
        RaidCraft.LOGGER.info("registered action: " + identifier);
    }

    public void unregisterAction(@NonNull JavaPlugin plugin, @NonNull String identifier) {

        Action<?> action = actions.remove(identifier);
        if (action == null) action = actions.remove(plugin.getName() + "." + identifier);
        if (action != null) {
            RaidCraft.LOGGER.info("removed action: " + identifier + " (" + plugin.getName() + ")");
        }
    }

    public void unregisterActions(@NonNull JavaPlugin plugin) {

        actions.keySet().removeIf(key -> key.startsWith(plugin.getName().toLowerCase()));
        RaidCraft.LOGGER.info("removed all actions of: " + plugin.getName());
    }

    public Map<String, Action<?>> getActions() {

        return new HashMap<>(actions);
    }

    @SneakyThrows
    public Action<?> create(@NonNull String identifier, @NonNull ConfigurationSection config) {

        if (!actions.containsKey(identifier)) {
            throw new ActionException("unknown action: " + identifier);
        }
        return new ActionConfigWrapper<>(actions.get(identifier), config);
    }
}
