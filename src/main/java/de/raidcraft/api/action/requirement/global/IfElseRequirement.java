package de.raidcraft.api.action.requirement.global;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.requirement.Requirement;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author mdoering
 */
public class IfElseRequirement<T> implements Requirement<T> {

    @Override
    @SuppressWarnings("unchecked")
    @Information(value = "if-else", desc = "Compares two requirements against each other.", conf = {
            "left: leftside requirement", "right: rightside requirement", "operator: [&&][||]" })
    public boolean test(T type, ConfigurationSection config) {

        ConfigurationSection left = config.getConfigurationSection("left");
        ConfigurationSection right = config.getConfigurationSection("right");
        if (left == null || right == null)
            return false;
        Optional<? extends Requirement<T>> leftRequirement = ActionAPI.createRequirement(getIdentifier(),
                left.getString("type"), left, (Class<T>) type.getClass());
        Optional<? extends Requirement<T>> rightRequirement = ActionAPI.createRequirement(getIdentifier(),
                right.getString("type"), right, (Class<T>) type.getClass());
        if (!leftRequirement.isPresent() || !rightRequirement.isPresent()) {
            return false;
        }
        String operator = config.getString("operator", "&&");
        switch (operator) {
        case "&&":
        case "&":
            return leftRequirement.get().test(type) && rightRequirement.get().test(type);
        case "||":
        case "|":
            return leftRequirement.get().test(type) || rightRequirement.get().test(type);
        }
        return false;
    }
}
