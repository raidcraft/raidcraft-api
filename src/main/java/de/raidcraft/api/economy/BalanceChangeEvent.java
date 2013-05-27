package de.raidcraft.api.economy;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Philip Urban
 */
public class BalanceChangeEvent extends Event {

    private BalanceSource source;
    private String detail;
    private String accountName;
    private double amount;

    public BalanceChangeEvent(BalanceSource source, String detail, String accountName, double amount) {

        this.source = source;
        this.detail = detail;
        this.accountName = accountName;
        this.amount = amount;
    }

    public BalanceSource getSource() {

        return source;
    }

    public String getDetail() {

        return detail;
    }

    public String getAccountName() {

        return accountName;
    }

    public double getAmount() {

        return amount;
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
