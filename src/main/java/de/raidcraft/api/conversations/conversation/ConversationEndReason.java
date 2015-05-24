package de.raidcraft.api.conversations.conversation;

import de.raidcraft.util.EnumUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author mdoering
 */
public enum ConversationEndReason {

    ACTION("Plugin Action"),
    OUT_OF_RANGE("Ausser Reichweite!"),
    DEATH("Du bist gestorben."),
    START_NEW_CONVERSATION("Es wurde eine neue Unterhaltung begonnen."),
    ENDED("Unterhaltung beendet."),
    CUSTOM("Custom Conversation End Reason not defined!"),
    PLAYER_ABORT("Die Unterhaltung wurde abgerochen."),
    PLAYER_QUIT("Die Unterhaltung wurde abgebrochen da Du das Spiel verlassen hast."),
    PLAYER_CHANGED_WORLD("Die Unterhaltung wurde abgebrochen da Du die Welt gewechselt hast.");

    @Getter
    @Setter
    private String message;

    ConversationEndReason(String message) {

        this.message = message;
    }

    public static ConversationEndReason fromString(String val) {

        return EnumUtils.getEnumFromString(ConversationEndReason.class, val);
    }
}
