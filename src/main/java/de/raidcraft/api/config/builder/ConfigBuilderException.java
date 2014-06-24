package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.CommandException;

/**
 * @author mdoering
 */
public class ConfigBuilderException extends CommandException {

    public ConfigBuilderException(String message) {

        super(message);
    }

    public ConfigBuilderException(Throwable cause) {

        super(cause);
    }
}
