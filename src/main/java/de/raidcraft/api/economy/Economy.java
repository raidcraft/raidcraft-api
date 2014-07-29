package de.raidcraft.api.economy;

import org.bukkit.command.CommandSender;

/**
 * @author Philip Urban
 */
public interface Economy {

    public void createAccount(AccountType type, String accountName);

    public void deleteAccount(AccountType type, String accountName);

    public boolean accountExists(AccountType type, String accountName);

    public double getBalance(AccountType type, String accountName);

    public String getFormattedBalance(AccountType type, String accountName);

    public String getFormattedAmount(double amount);

    public double parseCurrencyInput(String input);

    public boolean hasEnough(AccountType type, String accountName, double amount);

    public void substract(AccountType type, String accountName, double amount);

    public void substract(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    public void add(AccountType type, String accountName, double amount);

    public void add(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    public void modify(AccountType type, String accountName, double amount);

    public void modify(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    public void set(AccountType type, String accountName, double amount);

    public void set(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    public String getCurrencyNameSingular();

    public String getCurrencyNamePlural();

    public void printFlow(CommandSender sender, AccountType type, String accountName, int entries);
}
