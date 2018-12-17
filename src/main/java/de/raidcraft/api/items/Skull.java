package de.raidcraft.api.items;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.base.Strings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author Philip
 */
public class Skull {

    private static final Pattern Base64Matcher = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");
    private static final String DIVIDER = "###";

    public static boolean isBase64(String base64String) {
        if (Strings.isNullOrEmpty(base64String)) return false;
        return Base64Matcher.matcher(base64String).matches();
    }

    public static boolean addHead(Player player, String skullOwner) {

        return addHead(player, skullOwner, 1);
    }

    public static boolean addHead(Player player, String skullOwner, int quantity) {

        PlayerInventory inv = player.getInventory();
        int firstEmpty = inv.firstEmpty();
        if (firstEmpty == -1) {
            return false;
        } else {
            inv.setItem(firstEmpty, getSkull(skullOwner, quantity));
            return true;
        }
    }

    public static String implode(Set<String> input, String glue) {

        int i = 0;
        StringBuilder output = new StringBuilder();
        for (String key : input) {
            if (i++ != 0) {
                output.append(glue);
            }
            output.append(key);
        }
        return output.toString();
    }

    public static String fixcase(String inputName) {

        String inputNameLC = inputName.toLowerCase();
        Player player = Bukkit.getServer().getPlayerExact(inputNameLC);

        if (player != null) {
            return player.getName();
        }

        for (OfflinePlayer offPlayer : Bukkit.getServer().getOfflinePlayers()) {
            if (offPlayer.getName().toLowerCase().equals(inputNameLC)) {
                return offPlayer.getName();
            }
        }

        return inputName;
    }

    public static ItemStack getSkull(String skullOwner) {

        if (skullOwner.contains(":")) {
            skullOwner = skullOwner.split(":")[1];
        }
        if (isBase64(skullOwner)) {
            return getSkullWithCustomSkin(skullOwner);
        }
        return getSkull(skullOwner, 1);
    }

    public static ItemStack getSkullWithCustomSkin(String base64Url) {

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);

        if (head.getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (!meta.hasOwner() || meta.getPlayerProfile() == null) {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.randomUUID()));
            }
            PlayerProfile profile = meta.getPlayerProfile();
            profile.setProperty(new ProfileProperty("textures", base64Url, null));
        }

        return head;
    }

    public static Optional<String> serializeSkull(Block block) {
        if (block == null || !(block.getState() instanceof org.bukkit.block.Skull)) {
            return Optional.empty();
        }

        org.bukkit.block.Skull skull = (org.bukkit.block.Skull) block.getState();

        if (skull.getPlayerProfile() == null || !skull.getPlayerProfile().hasTextures()) {
            return Optional.empty();
        }

        return skull.getPlayerProfile().getProperties().stream().findFirst().map(profileProperty ->
                skull.getOwningPlayer().getUniqueId().toString() +
                DIVIDER +
                profileProperty.getName() +
                DIVIDER +
                profileProperty.getValue() +
                DIVIDER +
                profileProperty.getSignature());
    }

    public static void applySerializedSkull(Block block, String serializedSkull) {

        if (Strings.isNullOrEmpty(serializedSkull) || block == null) return;
        if (!(block.getState() instanceof org.bukkit.block.Skull)) return;

        String[] split = serializedSkull.split(DIVIDER);
        if (split.length < 4) return;
        UUID uuid = UUID.fromString(split[0]);
        String name = split[1];
        String value = split[2];
        String signature = split[3];

        org.bukkit.block.Skull skull = (org.bukkit.block.Skull) block.getState();
        if (!skull.hasOwner() || skull.getPlayerProfile() == null) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        }

        PlayerProfile playerProfile = skull.getPlayerProfile();
        playerProfile.setProperty(new ProfileProperty(name, value, signature));
        skull.setPlayerProfile(playerProfile);

        skull.update(true);
    }

    public static ItemStack getSkull(String skullOwner, int quantity) {

        // TODO: allow base64 skulls
        String skullOwnerLC = skullOwner.toLowerCase();

        for (CustomSkullType skullType : CustomSkullType.values()) {
            if (skullOwnerLC.equals(skullType.getSpawnName().toLowerCase())) {
                return getSkull(skullType, quantity);
            }
        }

        switch (skullOwnerLC) {
            case "HEAD_SPAWN_CREEPER":
                return getSkull(SkullType.CREEPER, quantity);
            case "HEAD_SPAWN_ZOMBIE":
                return getSkull(SkullType.ZOMBIE, quantity);
            case "HEAD_SPAWN_SKELETON":
                return getSkull(SkullType.SKELETON, quantity);
            case "HEAD_SPAWN_WITHER":
                return getSkull(SkullType.WITHER, quantity);
            default:
                return getSkull(skullOwner, null, quantity);
        }
    }

    public static ItemStack getSkull(String skullOwner, String displayName) {

        return getSkull(skullOwner, displayName, 1);
    }

    public static ItemStack getSkull(String skullOwner, String displayName, int quantity) {

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, quantity, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(skullOwner);
        if (displayName != null) {
            skullMeta.setDisplayName(ChatColor.RESET + displayName);
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static ItemStack getSkull(CustomSkullType type) {

        return getSkull(type, 1);
    }

    public static ItemStack getSkull(CustomSkullType type, int quantity) {

        return getSkull(type.getOwner(), type.getDisplayName(), quantity);
    }

    public static ItemStack getSkull(SkullType type) {

        return getSkull(type, 1);
    }

    public static ItemStack getSkull(SkullType type, int quantity) {

        return new ItemStack(Material.PLAYER_HEAD, quantity, (short) type.ordinal());
    }

    public static String format(String text, String... replacement) {

        String output = text;
        for (int i = 0; i < replacement.length; i++) {
            output = output.replace("%" + (i + 1) + "%", replacement[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', output);
    }

    public static void formatMsg(CommandSender player, String text, String... replacement) {

        player.sendMessage(format(text, replacement));
    }

    public static String formatStrip(String text, String... replacement) {

        return ChatColor.stripColor(format(text, replacement));
    }
}
