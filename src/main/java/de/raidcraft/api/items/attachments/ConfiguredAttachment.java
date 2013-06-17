package de.raidcraft.api.items.attachments;

import de.raidcraft.api.config.DataMap;

import java.util.HashMap;

/**
 * @author Silthus
 */
public class ConfiguredAttachment extends DataMap {

    private final String name;
    private final String provider;
    private final String description;

    public ConfiguredAttachment(String name, String provider, String description) {

        super(new HashMap<>());
        this.name = name;
        this.provider = provider;
        this.description = description;
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
}
