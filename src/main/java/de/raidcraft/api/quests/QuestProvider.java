package de.raidcraft.api.quests;

import de.raidcraft.api.config.ConfigLoader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public interface QuestProvider {

    void registerQuestConfigLoader(ConfigLoader loader);

    ConfigLoader getQuestConfigLoader(String suffix);

    boolean hasQuestItem(Player player, ItemStack itemStack, int amount);

    void removeQuestItem(Player player, ItemStack... itemStack);

    void addQuestItem(Player player, ItemStack... itemStack);
}
