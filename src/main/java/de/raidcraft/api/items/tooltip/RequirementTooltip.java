package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.CustomItemUtil;

import java.util.Map;

/**
 * @author Silthus
 */
public class RequirementTooltip extends Tooltip {

    private final Map<String, String> requirements = new CaseInsensitiveMap<>();

    public RequirementTooltip(String attachmentName, String message) {

        super(TooltipSlot.REQUIREMENT);
        requirements.put(attachmentName, message);
    }

    public void addRequirement(String name, String msg) {

        requirements.put(name, msg);
    }

    public boolean hasRequirement(String name) {

        return requirements.containsKey(name);
    }

    public String getRequirement(String name) {

        return requirements.get(name);
    }

    @Override
    protected void updateLineWidth() {

        for (String msg : requirements.values()) {
            int width = CustomItemUtil.getStringWidth(msg);
            if (width > getWidth()) {
                setWidth(width);
            }
        }
    }

    @Override
    public String[] getTooltip() {

        String[] strings = new String[requirements.size()];
        int i = 0;
        for (String msg : requirements.values()) {
            strings[i] = msg;
            i++;
        }
        return strings;
    }
}
