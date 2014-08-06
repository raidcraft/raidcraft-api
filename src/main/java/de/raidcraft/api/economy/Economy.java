package de.raidcraft.api.economy;

import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public interface Economy {

    public void createAccount(UUID player);

    public void createAccount(AccountType type, String accountName);

    public void deleteAccount(UUID player);

    public void deleteAccount(AccountType type, String accountName);

    public boolean accountExists(UUID player);

    public boolean accountExists(AccountType type, String accountName);

    public double getBalance(UUID player);

    public double getBalance(AccountType type, String accountName);

    public String getFormattedBalance(UUID player);

    public String getFormattedBalance(AccountType type, String accountName);

    public String getFormattedAmount(double amount);

    public double parseCurrencyInput(String input);

    public boolean hasEnough(UUID player, double amount);

    public boolean hasEnough(AccountType type, String accountName, double amount);

    public void substract(UUID player, double amount);

    public void substract(AccountType type, String accountName, double amount);

    public void substract(UUID player, double amount, BalanceSource source, String detail);

    public void substract(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    public void add(UUID player, double amount);

    public void add(AccountType type, String accountName, double amount);

    public void add(UUID player, double amount, BalanceSource source, String detail);

    public void add(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    public void modify(UUID player, double amount);

    public void modify(AccountType type, String accountName, double amount);

    public void modify(UUID player, double amount, BalanceSource source, String detail);

    public void modify(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    public void set(UUID player, double amount);

    public void set(AccountType type, String accountName, double amount);

    public void set(UUID player, double amount, BalanceSource source, String detail);

    public void set(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    public String getCurrencyNameSingular();

    public String getCurrencyNamePlural();

    public void printFlow(CommandSender sender, AccountType type, String accountName, int entries);

    // depracted stuff

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

    @Deprecated
    public void printFlow(CommandSender sender, String accountName, int entries);
}
