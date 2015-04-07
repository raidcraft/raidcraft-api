package de.raidcraft.api.quests;

import de.raidcraft.api.quests.host.QuestHost;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public interface QuestProvider {

    public void registerQuestHost(String type, Class<? extends QuestHost> clazz);

    public void registerQuestConfigLoader(QuestConfigLoader loader);

    public QuestConfigLoader getQuestConfigLoader(String suffix);

    public QuestHost getQuestHost(String id) throws InvalidQuestHostException;

    public boolean hasQuestItem(Player player, ItemStack itemStack, int amount);

    public void removeQuestItem(Player player, ItemStack... itemStack);

    public void addQuestItem(Player player, ItemStack... itemStack);
}
