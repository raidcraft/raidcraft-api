package de.raidcraft.api.items.tooltip;

import de.raidcraft.api.items.attachments.RequiredItemAttachment;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.CustomItemUtil;

import java.util.Map;

/**
 * @author Silthus
 */
public class RequirementTooltip extends Tooltip {

    private final Map<String, RequiredItemAttachment> requirements = new CaseInsensitiveMap<>();

    public RequirementTooltip(RequiredItemAttachment requirement) {

        super(TooltipSlot.REQUIREMENT);
        requirements.put(requirement.getName(), requirement);
    }

    public void addRequirement(RequiredItemAttachment requirement) {

        requirements.put(requirement.getName(), requirement);
    }

    public boolean hasRequirement(String name) {

        return requirements.containsKey(name);
    }

    public RequiredItemAttachment getRequirement(String name) {

        return requirements.get(name);
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

    @Override
    public String[] getTooltip() {

        String[] strings = new String[requirements.size()];
        int i = 0;
        for (RequiredItemAttachment requirement : requirements.values()) {
            strings[i] = requirement.getItemText();
            i++;
        }
        return strings;
    }
}
