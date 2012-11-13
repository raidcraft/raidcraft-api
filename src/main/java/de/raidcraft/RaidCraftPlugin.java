package de.raidcraft;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author Silthus
 */
public class RaidCraftPlugin extends BasePlugin implements Component {

    private Economy economy;
    private Chat chat;
    private Permission permission;

    @Override
    public void enable() {

        Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
        if (plugin != null) {
            setupEconomy();
            setupChat();
            setupPermissions();
        }
        RaidCraft.registerComponent(RaidCraftPlugin.class, this);
    }

    @Override
    public void disable() {
        //TODO: implement
    }

    public Economy getEconomy() {

        return economy;
    }

    public Chat getChat() {

        return chat;
    }

    public Permission getPermission() {

        return permission;
    }

    private boolean setupPermissions() {

        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat() {

        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy() {

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
