package de.raidcraft.api.chat;

import com.google.common.base.Strings;
import de.raidcraft.api.BasePlugin;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author mdoering
 */
public class Chat {

    @Getter
    private static Map<Character, AutoCompletionProvider> autoCompletionProviders = new HashMap<>();

    public static void registerAutoCompletionProvider(BasePlugin plugin, AutoCompletionProvider completionProvider) {

        if (autoCompletionProviders.containsKey(completionProvider.getToken())) {
            plugin.getLogger().warning("AutoCompletionProvider with token " + completionProvider.getToken() + " already exists!");
            return;
        }
        autoCompletionProviders.put(completionProvider.getToken(), completionProvider);
        plugin.getLogger().info("Registered AutoCompletionProvider with token: " + completionProvider.getToken());
    }

    public static AutoCompletionProvider unregisterAutoCompletionProvider(BasePlugin plugin, AutoCompletionProvider completionProvider) {

        AutoCompletionProvider remove = autoCompletionProviders.remove(completionProvider.getToken());
        if (remove != null) {
            plugin.getLogger().info("Unregistered AutoCompletionProvider with token: " + remove.getToken());
        }
        return remove;
    }

    public static FancyMessage replaceMatchingAutoCompleteItems(Player player, String message) {

        FancyMessage msg = new FancyMessage("");
        for (AutoCompletionProvider provider : autoCompletionProviders.values()) {
            Matcher matcher = provider.getAUTO_COMPLETE_PATTERN().matcher(message);
            if (matcher.matches()) {
                msg = matchAndReplaceItem(provider, player, msg, message);
            } else {
                msg = msg.then(message);
            }
        }
        return msg;
    }

    /**
     * Recursivly matches item names in the message and replaces them with nice thumbnails. For example:
     * <code>foo bar ?"item1" bar foo ?"item2" foobar</code> will match the following groups:
     * 0: [0,40] foo bar ?"item1" bar foo ?"item2" foobar
     * 1: [0,25] foo bar ?"item1" bar foo
     * 2: [27,32] item2
     * 3: [33,40] foobar
     *
     * @param msg     object to populate
     * @param message to replace
     *
     * @return same {@link mkremins.fanciful.FancyMessage} object with replaced items
     */
    private static FancyMessage matchAndReplaceItem(AutoCompletionProvider provider, Player player, FancyMessage msg, String message) {

        Matcher matcher = provider.getAUTO_COMPLETE_PATTERN().matcher(message);
        if (matcher.matches()) {
            // check if the message starts directly with the item
            // ?"itemName" foo bar
            if (Strings.isNullOrEmpty(matcher.group(1))) {
                msg = provider.autoComplete(player, msg, matcher.group(2));
                if (!Strings.isNullOrEmpty(matcher.group(3))) {
                    msg.then(matcher.group(3));
                }
                return msg;
            }
            // lets recursivly match the text before the current match
            msg = matchAndReplaceItem(provider, player, msg, matcher.group(1));
            msg = provider.autoComplete(player, msg, matcher.group(2));
            if (!Strings.isNullOrEmpty(matcher.group(3))) {
                msg.then(matcher.group(3));
            }
            return msg;
        } else {
            return msg.then(message);
        }
    }
}
