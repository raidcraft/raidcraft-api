package de.raidcraft.api.chat;

import lombok.Data;
import mkremins.fanciful.FancyMessage;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
@Data
public abstract class AutoCompletionProvider {

    private final Pattern AUTO_COMPLETE_PATTERN;

    private final char token;
    private final int minLength;
    private final String errorMessage;

    public AutoCompletionProvider(char token, int minLength, String errorMessage) {

        this.token = token;
        this.minLength = minLength;
        this.errorMessage = errorMessage;
        this.AUTO_COMPLETE_PATTERN = Pattern.compile("(.*)" + (token == '?' ? "\\?" : token) + "\"([a-zA-ZüöäÜÖÄß\\s\\d]+)\"(.*)");
    }

    public AutoCompletionProvider(char token, String errorMessage) {

        this(token, 0, errorMessage);
    }

    /**
     * Gets a wrapped list of AutoComplete items with token and quotes.
     *
     * @param message to autocomplete, can be null
     * @return list of quoted items with the token in front
     */
    public final List<String> getAutoCompleteItems(Player player, @Nullable String message) {

        if (message != null && message.startsWith("\"")) {
            message = message.length() > 1 ? message.substring(1) : null;
        }
        return getAutoCompleteList(player, message).stream()
                .map(item -> token + "\"" + item + "\"")
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all matched AutoComplete items without token and quotes.
     *
     * @param message to autocomplete, can be null
     * @return list of autocomplete items without quotes and token
     */
    protected abstract List<String> getAutoCompleteList(Player player, @Nullable String message);

    /**
     * Auto completes the given item returning a {@link mkremins.fanciful.FancyMessage} object.
     * Classes implementing this should append to the given FancyMessage object with {@link mkremins.fanciful.FancyMessage#then()}
     *
     * @param player to autocomplete for
     * @param fancyMessage object to append to with then
     * @param item to autocomplete
     * @return auto completed item wrapped as FancyMessage
     */
    public abstract FancyMessage autoComplete(Player player, FancyMessage fancyMessage, String item);
}
