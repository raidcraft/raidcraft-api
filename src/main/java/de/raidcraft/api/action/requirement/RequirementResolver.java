package de.raidcraft.api.action.requirement;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author mdoering
 */
public interface RequirementResolver<T> {

    public List<Requirement<T>> getRequirements();

    public default boolean isMeetingAllRequirements(T object) {

        return getRequirements().stream().allMatch(requirement -> requirement.test(object));
    }

    @SuppressWarnings("unchecked")
    public default String getResolveReason(T object) {

        for (Requirement<T> requirement : getRequirements()) {
            if (!requirement.test(object)) {
                if (requirement instanceof Reasonable) {
                    return ((Reasonable<T>) requirement).getReason(object);
                } else {
                    return "Reason not found!";
                }
            }
        }
        return "Alle Vorraussetzungen sind erfüllt.";
    }
}
