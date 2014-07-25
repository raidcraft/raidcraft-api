package de.raidcraft.api.npc;

import de.raidcraft.RaidCraft;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.SimpleNPCDataStore;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.util.Storage;
import net.citizensnpcs.api.util.YamlStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handle all NPC's
 * User: IDragonfire
 */
public class NPC_Manager implements Listener {

    private static NPC_Manager INSTANCE;
    private Map<String, NPCRegistry> register = new HashMap<>();
    private Map<String, NPCDataStore> stores = new HashMap<>();
    private Map<String, Storage> saves = new HashMap<>();

    // Singleton
    private NPC_Manager() {

        if (CitizensAPI.hasImplementation()) {
            RaidCraft.LOGGER.warning("Citiziens not loaded! NPC_Manager not available");
            return;
        }
        // save all NPC's if server shut down
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("RaidCraft-API"));
    }

    public static NPC_Manager getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new NPC_Manager();
        }
        return INSTANCE;
    }

    /**
     * Remove a NPC from the server and data structure
     */
    public void removeNPC(NPC npc, String host) {

        npc.despawn(DespawnReason.REMOVAL);
        for (Trait trait : npc.getTraits()) {
            trait.onRemove();
        }
        npc.getOwningRegistry().deregister(npc);
        store(host);
    }

    public void removeNPC(UUID npcID, String host) {

        this.removeNPC(getNPC(npcID, host), host);
    }

    public NPC getNPC(UUID npcID, String host) {

        return this.register.get(host).getByUniqueId(npcID);
    }

    /**
     * Create a new NPC Registry for a special host. Also load old NPCs.
     *
     * @return
     */
    private NPCRegistry createNPCRegistry(String host) {

        File f = new File(CitizensAPI.getDataFolder() + File.separator
                + host + ".yml");
        Storage save = new YamlStorage(f);
        this.saves.put(host, save);

        // load old npcs
        if (f.exists()) {
            save.load();
        }
        NPCDataStore store = SimpleNPCDataStore.create(save);
        stores.put(host, store);
        NPCRegistry registry = CitizensAPI.createNamedNPCRegistry(host, store);
        // transfer into registry
        store.loadInto(registry);
        return registry;
    }

    // TODO: optimize save
    public NPC createPersistNpc(String name, String host) {

        if (!register.containsKey(host)) {
            register.put(host, createNPCRegistry(host));
        }
        NPC npc = register.get(host).createNPC(EntityType.PLAYER, name);
        store(host);
        return npc;
    }

    // TODO: optimize save
    public NPC spawnPersistNpc(Location loc, String name, String host) {

        NPC npc = this.createPersistNpc(name, host);
        npc.spawn(loc);
        store(host);
        return npc;
    }

    /**
     * You must register custom Traits over this method BEFORE you load the custom npc's
     *
     * @param trait     class for custom traits
     * @param traitname name to identify and store trait
     */
    public void registerTrait(Class<? extends Trait> trait, String traitname) {

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(trait).withName(traitname));
    }

    /**
     * ATTENTION: load NPCs after you register all customs traits
     *
     * @param host name of the holder of the npcs, typically the plugin name
     *
     * @see #registerTrait(Class, String)
     */
    public void loadNPCs(String host) {

        register.put(host, createNPCRegistry(host));
    }

    public void store(String host) {

        for (NPC npc : this.register.get(host)) {
            this.stores.get(host).store(npc);
        }
        this.stores.get(host).saveToDiskImmediate();
    }

    public void storeAll() {

        for (String host : this.stores.keySet()) {
            store(host);
        }
    }

    private void storeImmediate(String host) {

        for (NPC npc : this.register.get(host)) {
            this.stores.get(host).store(npc);
        }
        this.stores.get(host).saveToDiskImmediate();
    }

    private void saveToDiskImmediate() {

        for (String host : this.stores.keySet()) {
            storeImmediate(host);
        }
    }

    @EventHandler
    private void pluginDisable(PluginDisableEvent event) {

        this.saveToDiskImmediate();
    }

    public boolean isNPC(Entity entity) {

        return entity.hasMetadata("NPC");
    }
}
