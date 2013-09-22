package de.raidcraft.api.items.attachments;

import de.raidcraft.api.config.DataMap;
import org.bukkit.ChatColor;

import java.util.HashMap;

/**
 * @author Silthus
 */
public class ConfiguredAttachment extends DataMap {

    private final String name;
    private final String provider;
    private final String description;
    private final ChatColor color;

    public ConfiguredAttachment(String name, String provider, String description, ChatColor color) {

        super(new HashMap<>());
        this.name = name;
        this.provider = provider;
        this.description = description;
        this.color = color;
    }

    public String getAttachmentName() {

        return name;
    }

    public String getProvider() {

        return provider;
    }

    public String getDescription() {

        return description;
    }

    public ChatColor getColor() {

        return color;
    }
}
