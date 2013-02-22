package de.raidcraft.api.conversation;

import java.util.List;

/**
 * @author Silthus
 */
public interface Stage<T> {

    public String getName();

    public Conversation getConversation();

    public List<Action<T>> getActions();

    public List<Option<T>> getOptions();

    public Option<T> chooseOption(int index) throws InvalidChoiceException;

    public Option<T> chooseOption(String alias) throws InvalidChoiceException;
}
