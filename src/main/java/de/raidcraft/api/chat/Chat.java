package de.raidcraft.api.chat;

import com.google.common.base.Strings;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.util.fanciful.FancyMessage;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static FancyMessage replaceMatchingAutoCompleteItems(Player player, String message, FancyMessage fancyMessage) {

        List<AutoCompletionProvider> matchingProviders = getMatchingProviders(message);
        if (matchingProviders.isEmpty()) {
            return fancyMessage.then(message);
        }
        return matchAndReplaceItem(matchingProviders, player, fancyMessage, message);
    }

    public static FancyMessage replaceMatchingAutoCompleteItems(Player player, String message) {

        return replaceMatchingAutoCompleteItems(player, message, new FancyMessage(""));
    }

    private static List<AutoCompletionProvider> getMatchingProviders(String message) {

        List<AutoCompletionProvider> matchingProviders = new ArrayList<>();
        for (AutoCompletionProvider provider : autoCompletionProviders.values()) {
            Matcher matcher = provider.getAUTO_COMPLETE_PATTERN().matcher(message);
            if (matcher.matches()) {
                matchingProviders.add(provider);
            }
        }
        return matchingProviders;
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
     * @return same {@link FancyMessage} object with replaced items
     */
    private static FancyMessage matchAndReplaceItem(List<AutoCompletionProvider> providers, Player player, FancyMessage msg, String message) {

        for (AutoCompletionProvider provider : providers) {
            Matcher matcher = provider.getAUTO_COMPLETE_PATTERN().matcher(message);
            if (matcher.matches()) {
                // check if the message starts directly with the item
                // ?"itemName" foobar
                if (Strings.isNullOrEmpty(matcher.group(1))) {
                    msg = provider.autoComplete(player, msg, matcher.group(2).replace("_", " "));
                    if (!Strings.isNullOrEmpty(matcher.group(3))) {
                        msg = matchAndReplaceItem(getMatchingProviders(matcher.group(3)), player, msg, matcher.group(3));
                    }
                    return msg;
                }
                // lets recursivly match the text before the current match
                msg = matchAndReplaceItem(getMatchingProviders(matcher.group(1)), player, msg, matcher.group(1));
                msg = provider.autoComplete(player, msg, matcher.group(2).replace("_", " "));
                if (!Strings.isNullOrEmpty(matcher.group(3))) {
                    msg = matchAndReplaceItem(getMatchingProviders(matcher.group(3)), player, msg, matcher.group(3));
                }
                return msg;
            }
        }
        return msg.then(message);
    }
}
