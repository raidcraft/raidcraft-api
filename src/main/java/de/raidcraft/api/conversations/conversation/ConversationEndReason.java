package de.raidcraft.api.conversations.conversation;

import de.raidcraft.util.EnumUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author mdoering
 */
public enum ConversationEndReason {

    ACTION("Plugin Action", true),
    OUT_OF_RANGE("Ausser Reichweite!", false),
    DEATH("Du bist gestorben.", false),
    START_NEW_CONVERSATION("Es wurde eine neue Unterhaltung begonnen.", true),
    ENDED("Unterhaltung beendet.", false),
    CUSTOM("Custom Conversation End Reason not defined!", false),
    PLAYER_ABORT("Die Unterhaltung wurde abgerochen.", false),
    PLAYER_QUIT("Die Unterhaltung wurde abgebrochen da Du das Spiel verlassen hast.", false),
    PLAYER_CHANGED_WORLD("Die Unterhaltung wurde abgebrochen da Du die Welt gewechselt hast.", false),
    ERROR("Error occured! Please see console or inform an admin...", false),
    SILENT("", true);

    @Getter
    @Setter
    private String message;
    @Getter
    private final boolean silent;

    ConversationEndReason(String message, boolean silent) {

        this.message = message;
        this.silent = silent;
    }

    public static ConversationEndReason fromString(String val) {

        return EnumUtils.getEnumFromString(ConversationEndReason.class, val);
    }
}
