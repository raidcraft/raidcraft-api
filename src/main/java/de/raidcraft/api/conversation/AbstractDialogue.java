package de.raidcraft.api.conversation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractDialogue implements Dialogue {

    private final Conversation conversation;
    private final Dialogue parent;
    private final List<Dialogue> options = new ArrayList<>();

    public AbstractDialogue(Conversation conversation, Dialogue parent) {

        this.conversation = conversation;
        this.parent = parent;
    }

    public AbstractDialogue(Conversation conversation) {

        this(conversation, null);
    }

    @Override
    public Conversation getConversation() {

        return conversation;
    }

    @Override
    public Dialogue getParent() {

        return parent;
    }

    @Override
    public List<Dialogue> getOptions() {

        return options;
    }

    protected void addOption(Dialogue dialogue) {

        this.options.add(dialogue);
    }

    @Override
    public Dialogue choose(int id) throws InvalidDialogueException {

        try {
            Dialogue dialogue = options.get(id);
            getConversation().setCurrentDialogue(dialogue);
            return dialogue;
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidDialogueException("Ungültige Option gewählt.");
        }
    }

    @Override
    public Dialogue back() throws InvalidDialogueException {

        Dialogue dialogue = getParent();
        if (dialogue != null) {
            getConversation().setCurrentDialogue(dialogue);
            return dialogue;
        } else {
            throw new InvalidDialogueException("Keine zurück Funktion verfügbar.");
        }
    }
}
