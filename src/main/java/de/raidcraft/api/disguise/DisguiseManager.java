/** **************************************************************
 *                      Tales of Faldoria                       *
 *                                                              *
 *  This plugin was written for Tales of Faldoria and is not    *
 *  for public use                                              *
 *                                                              *
 * Website: https://www.faldoria.de                             *
 * Contact: info@faldoria.de                                    *
 *                                                              *
 *************************************************************** */
package de.raidcraft.api.disguise;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.Component;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author xanily
 */
public class DisguiseManager implements Component {

    public static final String GAME_PROFILE_TEXTURE_PROPERTY = "textures";

    private final RaidCraftPlugin plugin;

    public DisguiseManager(RaidCraftPlugin plugin) {
        this.plugin = plugin;
        plugin.registerCommands(DisguiseCommand.class);

        RaidCraft.registerComponent(DisguiseManager.class, this);
    }

    /**
     * Creates a new disguise and saves it into the database.
     *
     * @param player to create disguise from
     * @param disguiseName can be null. Then a random name will be generated.
     * @return created disguise or empty optional if game profile had no textures.
     */
    public Optional<Disguise> createDisguise(Player player, String disguiseName) {
        Validate.notNull(player);

        Disguise disguise = null;
        PlayerDisguise playerDisguise = new PlayerDisguise(player);

        Map<String, Collection<WrappedSignedProperty>> profileProperties = playerDisguise.getGameProfile().getProperties().asMap();
        for (String data : profileProperties.keySet()) {
            for (WrappedSignedProperty wrappedSignedProperty : profileProperties.get(data)) {
                disguise = new Disguise(disguiseName, wrappedSignedProperty.getValue(), wrappedSignedProperty.getSignature());
            }
        }
        Optional<Disguise> optionalDisguise = Optional.ofNullable(disguise);
        optionalDisguise.ifPresent(Disguise::save);
        return optionalDisguise;
    }

    public Disguise createDisguise(String alias, String texture, String signature) {

        Disguise disguise = new Disguise(alias, texture, signature);
        disguise.save();
        return disguise;
    }

    /**
     * Queries the database for a disguise with the given alias.
     *
     * @param alias to search disguise for
     * @return disguise if it exists
     */
    public Optional<Disguise> getDisguise(String alias) {

        return RaidCraft.getDatabase(RaidCraftPlugin.class).find(Disguise.class).where()
                .eq("alias", alias)
                .findOneOrEmpty();
    }

    /**
     * Queries the database for the given disguise alias and checks if it exists.
     *
     * @param disguiseName to check
     * @return true if disguise already exists
     */
    public boolean isAlreadyTaken(String disguiseName) {

        return getDisguise(disguiseName).isPresent();
    }
}
