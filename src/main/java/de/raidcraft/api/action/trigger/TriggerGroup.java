package de.raidcraft.api.action.trigger;

import de.raidcraft.api.action.TriggerFactory;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.List;

@Data
public abstract class TriggerGroup extends Trigger implements TriggerListener<Player> {

    private final List<TriggerFactory> trigger;

    protected TriggerGroup(String identifier) {
        super(identifier);
        this.trigger = loadTrigger();
    }

    protected abstract List<TriggerFactory> loadTrigger();

    @Override
    public Class<Player> getTriggerEntityType() {
        return Player.class;
    }

    @Override
    public boolean processTrigger(Player entity, Trigger trigger) {
        return false;
    }
}
