package de.raidcraft.api.action.trigger;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.TriggerFactory;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class TriggerGroup extends Trigger {

    private final String description;
    private final boolean enabled;
    private final boolean ordered;
    private final int required;
    private final List<TriggerFactory> trigger;

    private final Map<UUID, TriggerGroupPlayerListener> playerListeners = new HashMap<>();

    public TriggerGroup(String identifier, ConfigurationSection config) {
        super(identifier);
        description = config.getString("desc", "");
        enabled = config.getBoolean("enabled", true);
        ordered = config.getBoolean("ordered", false);
        required = config.getInt("required", 0);
        trigger = ActionAPI.createTrigger(config.getConfigurationSection("trigger"));
    }

    public void registerPlayer(Player player) {
        if (!playerListeners.containsKey(player.getUniqueId())) {
            TriggerGroupPlayerListener listener = new TriggerGroupPlayerListener(this, player);
            playerListeners.put(player.getUniqueId(), listener);
            listener.registerListeners();
        }
    }

    public void unregisterPlayer(Player player) {
        TriggerGroupPlayerListener listener = playerListeners.remove(player.getUniqueId());
        if (listener != null) {
            listener.unregisterListeners();
        }
    }
}
