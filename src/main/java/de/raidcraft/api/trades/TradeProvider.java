package de.raidcraft.api.trades;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public interface TradeProvider {

    public void registerTradeSet(String tradeSetName, ConfigurationSection tradeSetConfig);

    public boolean tradeSetExists(String tradeSetName);
}
