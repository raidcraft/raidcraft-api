package de.raidcraft.api.language;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;

import java.io.File;

/**
 * @author Silthus
 */
public class TranslationConfig<T extends BasePlugin> extends ConfigurationBase<T> {

    public TranslationConfig(T plugin, File file) {

        super(plugin, file);
    }
}
