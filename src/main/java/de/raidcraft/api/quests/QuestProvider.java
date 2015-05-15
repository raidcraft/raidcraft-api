package de.raidcraft.api.quests;

import de.raidcraft.api.quests.host.QuestHost;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public interface QuestProvider {

    void registerQuestHost(String type, Class<? extends QuestHost> clazz);

    void registerQuestConfigLoader(QuestConfigLoader loader);

    QuestConfigLoader getQuestConfigLoader(String suffix);

    QuestHost getQuestHost(String id) throws InvalidQuestHostException;

    boolean hasQuestItem(Player player, ItemStack itemStack, int amount);

    void removeQuestItem(Player player, ItemStack... itemStack);

    void addQuestItem(Player player, ItemStack... itemStack);
}
