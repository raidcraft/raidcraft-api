package de.raidcraft.api.action.trigger.global;

import com.google.common.base.Strings;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.locations.Locations;
import de.raidcraft.api.random.*;
import de.raidcraft.api.random.objects.ItemLootObject;
import de.raidcraft.api.random.objects.MoneyLootObject;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Silthus
 */
public class GlobalPlayerTrigger extends Trigger implements Listener {

    public GlobalPlayerTrigger() {

        super("player", "interact", "block.break", "block.place", "move", "craft", "death", "join", "shear", "milk", "fish");
    }

    @Information(
            value = "player.interact",
            desc = "Listens for player interaction (with certain blocks at the defined location).",
            conf = {
                    "x",
                    "y",
                    "z",
                    "world",
                    "block: DIRT"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {

        informListeners("interact", event.getPlayer(), config -> Locations.fromConfig(config, event.getPlayer()).map(location -> {
            Block block = event.getClickedBlock();

            if (!location.isBlockEquals(block.getLocation())) return false;

            Set<Material> blocks = new HashSet<>();
            if (config.isList("blocks")) {
                config.getStringList("blocks").forEach(b -> {
                    Material material = Material.matchMaterial(b);
                    if (material != null) {
                        blocks.add(material);
                    } else {
                        RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
                    }
                });
                return blocks.contains(block.getType());
            }
            return !config.isSet("block") || Material.matchMaterial(config.getString("block", "AIR")) == block.getType();
        }).orElse(false));
    }

    @Information(
            value = "player.craft",
            desc = "Listens for crafting of items (can be custom).",
            conf = {
                    "item: <rc1337/so43034/world.quest.named-item/WOOD:5>"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        informListeners("craft", event.getWhoClicked(), config -> {
            try {
                return !config.isSet("item") || RaidCraft.getSafeItem(config.getString("item")).isSimilar(event.getRecipe().getResult());
            } catch (CustomItemException e) {
                return false;
            }
        });
    }

    @Information(
            value = "player.death",
            desc = "Triggered when the player died."
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {

        informListeners("death", event.getEntity());
    }

    @Information(
            value = "player.block.break",
            desc = "Listens for block breaking and optionally at the defined location.",
            conf = {
                    "x",
                    "y",
                    "z",
                    "world: [current]",
                    "block: DIRT",
                    "blocks: List of blocks to listen for (overrides a single block)"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        informListeners("block.break", event.getPlayer(), config -> {

            boolean matches = Locations.fromConfig(config, event.getPlayer())
                    .map(location -> location.isBlockEquals(event.getBlock().getLocation()))
                    .orElse(false);

            if (!matches) return false;

            Set<Material> blocks = new HashSet<>();
            if (config.isList("blocks")) {
                config.getStringList("blocks").forEach(b -> {
                    Material material = Material.matchMaterial(b);
                    if (material != null) {
                        blocks.add(material);
                    } else {
                        RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
                    }
                });
                return blocks.contains(event.getBlock().getType());
            }

            Material block = Material.matchMaterial(config.getString("block", "AIR"));
            if (block == null) {
                RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
            }
            return block == null || block == Material.AIR || block == event.getBlock().getType();
        });
    }

    @Information(
            value = "player.block.place",
            desc = "Listens for block placing and optionally at the defined location.",
            conf = {
                    "x",
                    "y",
                    "z",
                    "world: [current]",
                    "block: DIRT",
                    "blocks: List of blocks to listen for (overrides a single block)"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {

        informListeners("block.place", event.getPlayer(), config -> {

            boolean matches = Locations.fromConfig(config, event.getPlayer())
                    .map(location -> location.isBlockEquals(event.getBlockPlaced().getLocation()))
                    .orElse(false);

            if (!matches) return false;

            Set<Material> blocks = new HashSet<>();
            if (config.isList("blocks")) {
                config.getStringList("blocks").forEach(b -> {
                    Material material = Material.matchMaterial(b);
                    if (material != null) {
                        blocks.add(material);
                    } else {
                        RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
                    }
                });
                return blocks.contains(event.getBlock().getType());
            }

            Material block = Material.matchMaterial(config.getString("block", "AIR"));
            if (block == null) {
                RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
            }
            return block == null || block == Material.AIR || block == event.getBlock().getType();
        });
    }

    @Information(
            value = "player.join",
            desc = "Triggers when a player joined the server."
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {

        informListeners("join", event.getPlayer());
    }

    @Information(
            value = "player.move",
            desc = "Triggers if the player is at or in a radius of the given location.",
            conf = {
                    "x",
                    "y",
                    "z",
                    "world: [current]",
                    "radius: [0]"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {

        if (!hasMoved(event)) return;

        // if no coordinates are defined always return true
        // otherwise check the coordinates and the radius
        informListeners("move", event.getPlayer(),
                config -> Locations.fromConfig(config, event.getPlayer())
                        .map(location -> location.isInRange(event.getTo())).orElse(false));
    }


    @Information(
            value = "player.milk",
            desc = "Triggers if the player holds an empty bucket and right clicks the defined entity (default: COW).",
            conf = {
                    "entity: COW",
                    "item: BUCKET",
                    "return: MILK_BUCKET"
            }
    )
    @EventHandler
    public void onPlayerMilk(PlayerInteractEntityEvent event) {

        informListeners("milk", event.getPlayer(), config -> {

            EntityType entityType = EntityType.valueOf(config.getString("entity", "COW"));
            Material material = Material.matchMaterial(config.getString("item", Material.BUCKET.name()));
            Optional<ItemStack> returnItem = RaidCraft.getItem(config.getString("return", Material.MILK_BUCKET.name()));

            if (material == null) {
                RaidCraft.LOGGER.warning("item: " + config.getString("item") + " does not exist in @player.milk! " + ConfigUtil.getFileName(config));
                return false;
            }
            if (!returnItem.isPresent()) {
                RaidCraft.LOGGER.warning("return: " + config.getString("return") + " does not exist! " + ConfigUtil.getFileName(config));
                return false;
            }

            if (!event.getRightClicked().getType().equals(entityType)) {
                return false;
            }

            EquipmentSlot hand = event.getHand();
            ItemStack item;
            switch (hand) {
                case OFF_HAND:
                    item = event.getPlayer().getInventory().getItemInOffHand();
                    break;
                default:
                    item = event.getPlayer().getInventory().getItemInMainHand();
                    break;
            }

            if (item != null && item.getType().equals(material)) {
                event.setCancelled(true);
                switch (hand) {
                    case OFF_HAND:
                        event.getPlayer().getInventory().setItemInOffHand(returnItem.get());
                        break;
                    default:
                        event.getPlayer().getInventory().setItemInMainHand(returnItem.get());
                        break;
                }
                return true;
            }
            return false;
        });
    }

    @Information(
            value = "player.shear",
            desc = "Triggers if the player shears a sheep.",
            conf = {
                    "item: item to drop (if not defined drops default)",
                    "loot-table: allows specifying a loot-table for the shear event"
            }
    )
    @EventHandler
    public void onPlayerShear(PlayerShearEntityEvent event) {

        informListeners("shear", event.getPlayer(), config -> {

            EntityType entityType = EntityType.valueOf(config.getString("entity", EntityType.SHEEP.name()));

            if (!event.getEntity().getType().equals(entityType)) return false;

            Optional<RDSTable> lootTable = RDS.getTable(config.getString("loot-table"));
            lootTable.ifPresent(rdsTable -> {
                event.setCancelled(true);
                if (event.getEntity() instanceof Sheep) {
                    ((Sheep) event.getEntity()).setSheared(true);
                }
                for (RDSObject object : rdsTable.loot()) {
                    if (object instanceof Spawnable) {
                        ((Spawnable) object).spawn(event.getEntity().getLocation());
                    } else if (object instanceof Obtainable) {
                        ((Obtainable) object).addTo(event.getPlayer());
                    }
                }
            });
            if (lootTable.isPresent()) return true;

            if (!config.isSet("item")) {
                return true;
            }

            Optional<ItemStack> item = RaidCraft.getItem(config.getString("item"));

            item.ifPresent(itemStack -> {
                event.setCancelled(true);
                if (event.getEntity() instanceof Sheep) {
                    ((Sheep) event.getEntity()).setSheared(true);
                }
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack);
            });

            if (!item.isPresent()) {
                RaidCraft.LOGGER.warning("Invalid item " + config.getString("item") + " inside " + ConfigUtil.getFileName(config));
                return false;
            }

            return true;
        });
    }

    @Information(
            value = "player.fish",
            desc = "Triggers if the player fishes.",
            conf = {
                    "item: item to drop (if not defined drops default)",
                    "loot-table: specify a loot-table to drop items from (overrides item config)"
            }
    )
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {

        if (!(event.getCaught() instanceof Item)) return;
        Item item = (Item) event.getCaught();

        informListeners("fish", event.getPlayer(), config -> {
            Optional<RDSTable> lootTable = RDS.getTable(config.getString("loot-table"));
            lootTable.ifPresent(rdsTable -> {
                for (RDSObject object : rdsTable.loot()) {
                    if (object instanceof ItemLootObject) {
                        item.setItemStack(((ItemLootObject) object).getItemStack());
                    } else if (object instanceof Obtainable) {
                        ((Obtainable) object).addTo(event.getPlayer());
                    } else if (object instanceof Spawnable) {
                        ((Spawnable) object).spawn(event.getHook().getLocation());
                    }
                }
            });
            if (lootTable.isPresent()) return true;

            RaidCraft.getItem(config.getString("item")).ifPresent(item::setItemStack);

            return true;
        });
    }

    @Information(
            value = "world.enter",
            desc = "Triggers when the player changes the world.",
            conf = {
                    "world: name of the new world",
                    "old_world: optional old world of the player"
            }
    )
    @EventHandler(ignoreCancelled = true)
    public void onWorldJoin(PlayerChangedWorldEvent event) {

        informListeners("world.enter", event.getPlayer(), config -> {
            if (config.isSet("old_world") && !event.getFrom().getName().equalsIgnoreCase(config.getString("old_world"))) {
                return false;
            }
            return Strings.isNullOrEmpty(config.getString("world"))
                    || event.getPlayer().getWorld().getName().equalsIgnoreCase(config.getString("world"));
        });
    }

    private boolean hasMoved(PlayerMoveEvent event) {
        // Did we move a block?
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            return true;
        }
        return false;
    }
}