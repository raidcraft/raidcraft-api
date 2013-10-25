package de.raidcraft.api.economy;

/**
 * @author Philip Urban
 */
public enum BalanceSource {

    SKILL("Skillsystem"),
    DRAGON_TRAVEL("Drachenreisen"),
    PAY_COMMAND("Spielerüberweisung"),
    ADMIN_COMMAND("Adminüberweisung"),
    LOOT_OBJECT("Loot-Objekt"),
    SELL_ITEM("Itemverkauf"),
    BUY_REGION("Grundstücks Kauf"),
    SELL_REGION("Grundstücks Verkauf"),
    PLUGIN("Plugin"),
    GUILD("Gilde"),
    TRADE("Handel");

    private String friendlyName;

    private BalanceSource(String friendlyName) {

        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {

        return friendlyName;
    }
}
