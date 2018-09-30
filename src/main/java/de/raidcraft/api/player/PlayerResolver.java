package de.raidcraft.api.player;

import de.raidcraft.api.Component;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerResolver extends Component {
    <T> UUID getUUIDfrom(T type);

    UUID convertPlayer(@NonNull String name);

    String getUUIDStringFromName(String name);

    String getNameFromUUID(UUID uuid);

    String getNameFromUUID(String uuid);

    String castUUID(CommandSender sender);

    int getPlayerId(Player player);

    int getPlayerId(UUID uuid);

    UUID getUuidFromPlayerId(int id);
}
