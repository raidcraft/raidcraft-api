package de.raidcraft.api.economy;

import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public interface Economy {

    void createAccount(UUID player);

    void createAccount(AccountType type, String accountName);

    void deleteAccount(UUID player);

    void deleteAccount(AccountType type, String accountName);

    boolean accountExists(UUID player);

    boolean accountExists(AccountType type, String accountName);

    double getBalance(UUID player);

    double getBalance(AccountType type, String accountName);

    String getFormattedBalance(UUID player);

    String getFormattedBalance(AccountType type, String accountName);

    String getFormattedAmount(double amount);

    double parseCurrencyInput(String input);

    boolean hasEnough(UUID player, double amount);

    boolean hasEnough(AccountType type, String accountName, double amount);

    void substract(UUID player, double amount);

    void substract(AccountType type, String accountName, double amount);

    void substract(UUID player, double amount, BalanceSource source, String detail);

    void substract(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    void add(UUID player, double amount);

    void add(AccountType type, String accountName, double amount);

    void add(UUID player, double amount, BalanceSource source, String detail);

    void add(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    void modify(UUID player, double amount);

    void modify(AccountType type, String accountName, double amount);

    void modify(UUID player, double amount, BalanceSource source, String detail);

    void modify(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    void set(UUID player, double amount);

    void set(AccountType type, String accountName, double amount);

    void set(UUID player, double amount, BalanceSource source, String detail);

    void set(AccountType type, String accountName, double amount, BalanceSource source, String detail);

    String getCurrencyNameSingular();

    String getCurrencyNamePlural();

    void printFlow(CommandSender sender, AccountType type, String accountName, int entries);

    void printFlow(CommandSender sender, UUID playerId, int entries);

    // depracted stuff

    @Deprecated
    void createAccount(String accountName);

    @Deprecated
    void deleteAccount(String accountName);

    @Deprecated
    boolean accountExists(String accountName);

    @Deprecated
    double getBalance(String accountName);

    @Deprecated
    String getFormattedBalance(String accountName);

    @Deprecated
    boolean hasEnough(String accountName, double amount);

    @Deprecated
    void substract(String accountName, double amount);

    @Deprecated
    void substract(String accountName, double amount, BalanceSource source, String detail);

    @Deprecated
    void add(String accountName, double amount);

    @Deprecated
    void add(String accountName, double amount, BalanceSource source, String detail);

    @Deprecated
    void modify(String accountName, double amount);

    @Deprecated
    void modify(String accountName, double amount, BalanceSource source, String detail);

    @Deprecated
    void set(String accountName, double amount);

    @Deprecated
    void set(String accountName, double amount, BalanceSource source, String detail);

    @Deprecated
    void printFlow(CommandSender sender, String accountName, int entries);
}
