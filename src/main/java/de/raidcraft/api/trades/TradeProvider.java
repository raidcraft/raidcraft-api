package de.raidcraft.api.trades;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public interface TradeProvider {

    void registerTradeSet(String tradeSetName, ConfigurationSection tradeSetConfig);

    boolean tradeSetExists(String tradeSetName);
}
