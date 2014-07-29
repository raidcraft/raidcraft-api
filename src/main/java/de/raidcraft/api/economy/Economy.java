package de.raidcraft.api.economy;

import org.bukkit.command.CommandSender;

/**
 * @author Philip Urban
 */
public interface Economy {

    @Deprecated
    public void createAccount(String accountName);

    @Deprecated
    public void deleteAccount(String accountName);

    @Deprecated
    public boolean accountExists(String accountName);

    @Deprecated
    public double getBalance(String accountName);

    @Deprecated
    public String getFormattedBalance(String accountName);

    public String getFormattedAmount(double amount);

    public double parseCurrencyInput(String input);

    @Deprecated
    public boolean hasEnough(String accountName, double amount);

    @Deprecated
    public void substract(String accountName, double amount);

    @Deprecated
    public void substract(String accountName, double amount, BalanceSource source, String detail);

    @Deprecated
    public void add(String accountName, double amount);

    @Deprecated
    public void add(String accountName, double amount, BalanceSource source, String detail);

    @Deprecated
    public void modify(String accountName, double amount);

    @Deprecated
    public void modify(String accountName, double amount, BalanceSource source, String detail);

    @Deprecated
    public void set(String accountName, double amount);

    @Deprecated
    public void set(String accountName, double amount, BalanceSource source, String detail);

    public String getCurrencyNameSingular();

    public String getCurrencyNamePlural();

    @Deprecated
    public void printFlow(CommandSender sender, String accountName, int entries);
}
