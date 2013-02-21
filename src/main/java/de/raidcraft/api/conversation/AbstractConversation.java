package de.raidcraft.api.conversation;

import de.raidcraft.RaidCraft;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractConversation implements Conversation {

    private final String name;
    private final String playerName;
    private final List<Dialogue> dialogues = new ArrayList<>();
    private Dialogue initialDialogue;
    private Dialogue currentDialogue;

    public AbstractConversation(String name, String playerName) {

        this.name = name;
        this.playerName = playerName;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getPlayerName() {

        return playerName;
    }

    @Override
    public List<Dialogue> getDialogues() {

        return dialogues;
    }

    protected void addDialogue(Dialogue dialogue) {

        dialogues.add(dialogue);
        if (initialDialogue == null) {
            initialDialogue = dialogue;
        }
    }

    @Override
    public Dialogue getInitialDialogue() {

        return initialDialogue;
    }

    @Override
    public Dialogue getCurrentDialogue() throws ConversationException {

        if (currentDialogue != null) {
            return currentDialogue;
        } else {
            throw new ConversationException("Die Unterhaltung hat noch nicht begonnen.");
        }
    }

    @Override
    public void setCurrentDialogue(Dialogue currentDialogue) {

        this.currentDialogue = currentDialogue;
    }

    @Override
    public Dialogue start() {

        if (initialDialogue != null) {
            this.currentDialogue = initialDialogue;
            return initialDialogue;
        } else {
            RaidCraft.LOGGER.warning("No dialogues configured for the conversation: " + getName());
        }
        return null;
    }

    @Override
    public Dialogue startAt(Dialogue dialogue) {

        initialDialogue = dialogue;
        return start();
    }

    @Override
    public Dialogue stop() {

        return currentDialogue;
    }
}
