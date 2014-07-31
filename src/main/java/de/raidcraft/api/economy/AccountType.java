package de.raidcraft.api.economy;

/**
 * @author Dragonfire
 */
public enum AccountType {
    PLAYER("Player"),
    PLUGIN("Plugin"),
    CITY("City");

    private String type;

    private AccountType(String friendlyName) {

        this.type = friendlyName;
    }

    public String getType() {

        return type;
    }
}
