package de.raidcraft.api.items.tooltip;

import de.raidcraft.api.items.attachments.RequiredItemAttachment;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.CustomItemUtil;
import org.bukkit.ChatColor;

import java.util.Map;

/**
 * @author Silthus
 */
public class RequirementTooltip extends Tooltip {

    private final Map<String, RequiredItemAttachment> requirements = new CaseInsensitiveMap<>();
    private ChatColor color = ChatColor.RED;

    public RequirementTooltip(RequiredItemAttachment requirement) {

        super(TooltipSlot.REQUIREMENT);
        requirements.put(requirement.getName(), requirement);
        buildTooltips();
    }

    private void buildTooltips() {

        String[] strings = new String[requirements.size()];
        int i = 0;
        for (RequiredItemAttachment requirement : requirements.values()) {
            strings[i] = color + requirement.getItemText();
            i++;
        }
        setTooltip(strings);
    }

    public void removeRequirement(RequiredItemAttachment requirement) {

        requirements.remove(requirement.getName());
        buildTooltips();
    }

    public void addRequirement(RequiredItemAttachment requirement) {

        requirements.put(requirement.getName(), requirement);
        buildTooltips();
    }

    public boolean hasRequirement(String name) {

        return requirements.containsKey(name);
    }

    public RequiredItemAttachment getRequirement(String name) {

        return requirements.get(name);
    }

    public void setColor(ChatColor color) {

        this.color = color;
    }

    @Override
    protected void updateLineWidth() {

        for (RequiredItemAttachment requirement : requirements.values()) {
            int width = CustomItemUtil.getStringWidth(requirement.getItemText());
            if (width > getWidth()) {
                setWidth(width);
            }
        }
    }
}
