package de.raidcraft.api.economy;

/**
 * @author Philip Urban
 */
public interface Economy {

    public void createAccount(String accountName);

    public void deleteAccount(String accountName);

    public boolean accountExists(String accountName);

    public double getBalance(String accountName);

    public String getFormattedBalance(String accountName);

    public String getFormattedAmount(double amount);

    public double parseCurrencyInput(String input);

    public boolean hasEnough(String accountName, double amount);

    public void substract(String accountName, double amount);

    public void substract(String accountName, double amount, BalanceSource source, String detail);

    public void add(String accountName, double amount);

    public void add(String accountName, double amount, BalanceSource source, String detail);

    public void modify(String accountName, double amount);

    public void modify(String accountName, double amount, BalanceSource source, String detail);

    public void set(String accountName, double amount);

    public void set(String accountName, double amount, BalanceSource source, String detail);

    public String getCurrencyNameSingular();

    public String getCurrencyNamePlural();
}
