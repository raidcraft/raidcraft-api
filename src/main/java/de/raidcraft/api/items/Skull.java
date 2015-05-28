package de.raidcraft.api.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.raidcraft.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author Philip
 */
public class Skull {

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
        return getSkull(skullOwner, 1);
    }

    public static ItemStack getSkull(String skullOwner, int quantity) {

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

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, quantity, (short) SkullType.PLAYER.ordinal());
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

        return new ItemStack(Material.SKULL_ITEM, quantity, (short) type.ordinal());
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

    static {

        CRAFT_META_SKULL_CLASS = ReflectionUtil.getNmsClass("org.bukkit.craftbukkit", "inventory", "CraftMetaSkull");
        TILE_ENTITY_SKULL_CLASS = ReflectionUtil.getNmsClass("net.minecraft.server", "TileEntitySkull");
        ENTITY_HUMAN_CLASS = ReflectionUtil.getNmsClass("net.minecraft.server", "EntityHuman");
        Class<?> blockPosition = ReflectionUtil.getNmsClass("net.minecraft.server", "BlockPosition");
        if (blockPosition != null) {
            try {
                BLOCK_POSITION_CONSTRUCTOR = blockPosition.getConstructor(int.class, int.class, int.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        Class<?> craftWorld = ReflectionUtil.getNmsClass("org.bukkit.craftbukkit", "CraftWorld");
        if (craftWorld != null) {
            try {
                CRAFT_WORLD_GET_HANDLE = craftWorld.getMethod("getHandle");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        Class<?> craftHumanEntity = ReflectionUtil.getNmsClass("org.bukkit.craftbukkit", "entity", "CraftHumanEntity");
        CRAFT_HUMAN_ENTITY_CLASS = craftHumanEntity;
        if (craftHumanEntity != null) {
            try {
                CRAFT_ENTITY_GET_HANDLE = craftHumanEntity.getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        Class<?> worldServer = ReflectionUtil.getNmsClass("net.minecraft.server", "WorldServer");
        if (worldServer != null && blockPosition != null) {
            try {
                WORLD_SERVER_GET_TILE_ENTITY = worldServer.getMethod("getTileEntity", blockPosition);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private static Class<?> CRAFT_META_SKULL_CLASS;
    private static Class<?> CRAFT_HUMAN_ENTITY_CLASS;
    private static Class<?> TILE_ENTITY_SKULL_CLASS;
    private static Class<?> ENTITY_HUMAN_CLASS;
    private static Constructor<?> BLOCK_POSITION_CONSTRUCTOR;
    private static Method CRAFT_WORLD_GET_HANDLE;
    private static Method CRAFT_ENTITY_GET_HANDLE;
    private static Method WORLD_SERVER_GET_TILE_ENTITY;

    public static void injectCustomSkin(String url, String name, Object object) {

        Optional<Object> gameProfileHolder = getGameProfileHolder(object);
        if (!gameProfileHolder.isPresent()) return;

        Optional<Field> gameProfileField = getGameProfileField(gameProfileHolder.get());
        if (!gameProfileField.isPresent()) return;

        try {
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
            gameProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + url + "\"}}}")));
            gameProfileField.get().set(gameProfileHolder.get(), gameProfile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Optional<Object> getGameProfileHolder(Object object) {

        if (CRAFT_META_SKULL_CLASS == null
                || TILE_ENTITY_SKULL_CLASS == null
                || BLOCK_POSITION_CONSTRUCTOR == null
                || CRAFT_WORLD_GET_HANDLE == null
                || WORLD_SERVER_GET_TILE_ENTITY == null
                || CRAFT_HUMAN_ENTITY_CLASS == null
                || ENTITY_HUMAN_CLASS == null) {
            return Optional.empty();
        }
        Object gameProfileHolder = null;
        if (object instanceof ItemStack) {
            if (((ItemStack) object).getType() != Material.SKULL_ITEM) Optional.empty();
            SkullMeta skullMeta = (SkullMeta) ((ItemStack) object).getItemMeta();
            gameProfileHolder = CRAFT_META_SKULL_CLASS.cast(skullMeta);
        } else if (object instanceof Block) {
            try {
                if (((Block) object).getType() != Material.SKULL) Optional.empty();
                Object handle = CRAFT_WORLD_GET_HANDLE.invoke(((Block) object).getWorld());
                Object blockPosition = BLOCK_POSITION_CONSTRUCTOR.newInstance(((Block) object).getX(), ((Block) object).getY(), ((Block) object).getZ());
                Object tileEntity = WORLD_SERVER_GET_TILE_ENTITY.invoke(handle, blockPosition);
                gameProfileHolder = TILE_ENTITY_SKULL_CLASS.cast(tileEntity);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        } else if (object instanceof HumanEntity) {
            try {
                Object craftEntityHuman = CRAFT_HUMAN_ENTITY_CLASS.cast(object);
                Object entityHuman = CRAFT_ENTITY_GET_HANDLE.invoke(craftEntityHuman);
                gameProfileHolder = ENTITY_HUMAN_CLASS.cast(entityHuman);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return Optional.ofNullable(gameProfileHolder);
    }

    private static Optional<Field> getGameProfileField(Object gameProfileHolder) {

        Field gameProfileField = null;
        for (Field field : gameProfileHolder.getClass().getDeclaredFields()) {
            if (GameProfile.class.isAssignableFrom(field.getType())) {
                gameProfileField = field;
            }
        }
        if (gameProfileField == null) return Optional.empty();
        gameProfileField.setAccessible(true);
        return Optional.of(gameProfileField);
    }
}
