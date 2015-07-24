package de.raidcraft.api.conversations.conversation;

import org.bukkit.entity.Player;

import java.util.regex.Matcher;

/**
 * @author mdoering
 */
public interface PlayerVariable {

    String replace(Matcher matcher, Player player);
}
