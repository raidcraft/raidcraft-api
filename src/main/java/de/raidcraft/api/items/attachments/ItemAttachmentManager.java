package de.raidcraft.api.items.attachments;

import de.raidcraft.api.Component;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.util.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class ItemAttachmentManager implements Component {

    // provider displayName | provider instance
    private final Map<String, ItemAttachmentProvider> itemAttachmentProvider = new HashMap<>();

    public void registerItemAttachmentProvider(ItemAttachmentProvider provider) throws RaidCraftException {

        if (!provider.getClass().isAnnotationPresent(ProviderInformation.class)) {
            throw new RaidCraftException("ItemAttachmentProvider needs to be annotated with @ProviderInformation(displayName) in class: "
                    + provider.getClass().getCanonicalName());
        }
        String providerName = StringUtils.formatName(provider.getClass().getAnnotation(ProviderInformation.class).value());
        if (itemAttachmentProvider.containsKey(providerName)) {
            throw new RaidCraftException("Found two duplicate ItemAttachmentProviders - " + providerName + "! " +
                    "Tried to register " + provider.getClass().getCanonicalName() + " but "
                    + itemAttachmentProvider.get(providerName).getClass().getCanonicalName() + " is already registered.");
        }
        itemAttachmentProvider.put(providerName, provider);
    }

    public ItemAttachmentProvider getAttachmentProvider(String name) throws RaidCraftException {

        name = StringUtils.formatName(name);
        if (!itemAttachmentProvider.containsKey(name)) {
            throw new RaidCraftException("No ItemAttachmentProvider with the displayName " + name + " was found!");
        }
        return itemAttachmentProvider.get(name);
    }

    public ItemAttachment getItemAttachment(String provider, String name, Player player) throws ItemAttachmentException {

        if (!itemAttachmentProvider.containsKey(provider)) {
            throw new ItemAttachmentException("ItemAttachmentProvider with the displayName " + provider + " was not found!");
        }
        return itemAttachmentProvider.get(provider).getItemAttachment(player, name);
    }
}
