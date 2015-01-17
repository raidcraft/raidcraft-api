package de.raidcraft.api.economy;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Philip Urban
 */
public class BalanceChangeEvent extends Event {

    @Getter
    private BalanceSource source;
    @Getter
    private String detail;
    @Getter
    private AccountType type;
    @Getter
    private String accountName;
    @Getter
    private double amount;

    public BalanceChangeEvent(BalanceSource source, String detail, AccountType type, String accountName, double amount) {

        this.source = source;
        this.detail = detail;
        this.accountName = accountName;
        this.type = type;
        this.amount = amount;
    }
    //<-- Handler -->//

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
