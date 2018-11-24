package de.raidcraft.api.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.EntityMetaData;
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
import net.citizensnpcs.trait.CurrentLocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * Handle all NPC's
 *
 * @author Dragonfire
 */
public class NPC_Manager {

    private static NPC_Manager INSTANCE;
    private Map<String, NPCRegistry> register = new CaseInsensitiveMap<>();
    private Map<String, NPCDataStore> stores = new CaseInsensitiveMap<>();
    private Map<String, Storage> saves = new CaseInsensitiveMap<>();
    private Map<String, NPCRegistry> nonPersistentRegistry = new CaseInsensitiveMap<>();

    // Singleton
    private NPC_Manager() {

        if (!CitizensAPI.hasImplementation()) {
            RaidCraft.LOGGER.warning("Citiziens not loaded! NPC_Manager not available");
        }
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
        if(stores.containsKey(host))
            store(host);
    }

    public void removeNPC(UUID npcID, String host) {

        NPC npc = getNPC(npcID, host);
        if (npc == null) return;
        this.removeNPC(npc, host);
    }

    public void removeAllNPCs(String host) {

        if(register.containsKey(host)) {
            for (NPC npc : register.get(host).sorted()) {
                removeNPC(npc, host);
            }
        } else if(nonPersistentRegistry.containsKey(host)) {
            for (NPC npc : nonPersistentRegistry.get(host).sorted()) {
                removeNPC(npc, host);
            }
        }
    }

    /**
     * Worked not for non persist NPC's
     */
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

    /**
     * Initialize the data structure of the NPC. This method must be called ONE time,
     * e.g. if a admin create a new DragonStation.
     * WARNING: Do not call this command on each server startStage/reload
     *
     * @param name npc displayName
     * @param host sttore file, e.g. a pluginname, componentname
     *
     * @return
     *
     * @see this.createNonPersistNpc
     */
    // TODO: optimize save
    public NPC createPersistNpc(String name, String host) {

        if (!register.containsKey(host)) {
            register.put(host, createNPCRegistry(host));
        }
        NPC npc = register.get(host).createNPC(EntityType.PLAYER, name);
        store(host);
        return npc;
    }


    /**
     * Initialize the data structure of the NPC and spawn it. This method must be called ONE time,
     * e.g. if a admin create a new DragonStation.
     * WARNING: Do not call this command on each server startStage/reload
     *
     * @param name npc displayName
     * @param host sttore file, e.g. a pluginname, componentname
     *
     * @return
     *
     * @see this.spawnNonPersistNpc
     */
    public NPC spawnPersistNpc(Location loc, String name, String host) {

        NPC npc = this.createPersistNpc(name, host);
        npc.addTrait(CurrentLocation.class);
        npc.getTrait(CurrentLocation.class).setLocation(loc);
        npc.spawn(loc);
        store(host);
        return npc;
    }

    public NPCRegistry createNonPersistentNpcRegistry() {

        return CitizensAPI.createAnonymousNPCRegistry(new NonPersitentNPCDataStore());
    }

    /**
     * Warning: NPC will not be saved on disk and is lost on reload/restart
     *
     * @param name npc displayName
     * @param host sttore file, e.g. a pluginname, componentname
     *
     * @return
     *
     * @see this.createPersistNpc
     */
    public NPC createNonPersistNpc(String name, String host) {

        return getNonPersistentNpcRegistry(host).createNPC(EntityType.PLAYER, name);
    }

    public NPC createNonPersistNpc(String name, String host, EntityType type) {

        return getNonPersistentNpcRegistry(host).createNPC(type, name);
    }

    public NPCRegistry getNonPersistentNpcRegistry(String host) {

        if (!nonPersistentRegistry.containsKey(host)) {
            nonPersistentRegistry.put(host, createNonPersistentNpcRegistry());
        }
        return nonPersistentRegistry.get(host);
    }

    /**
     * Warning: NPC will not be saved on disk and is lost on reload/restart
     *
     * @param name displayName of the npc
     * @param host sttore file, e.g. a pluginname, componentname
     *
     * @return
     *
     * @see this.spawnPersistNpc
     */
    public NPC spawnNonPersistNpc(Location loc, String name, String host) {

        NPC npc = this.createNonPersistNpc(name, host);
        npc.addTrait(CurrentLocation.class);
        npc.getTrait(CurrentLocation.class).setLocation(loc);
        npc.spawn(loc);
        return npc;
    }

    public NPC spawnNonPersistNpc(Location loc, String name, String host, EntityType entityType) {

        NPC npc = this.createNonPersistNpc(name, host, entityType);
        npc.addTrait(CurrentLocation.class);
        npc.getTrait(CurrentLocation.class).setLocation(loc);
        npc.spawn(loc);
        return npc;
    }


    /**
     * You must register custom Traits over this method BEFORE you load the custom npc's
     *
     * @param trait     class for custom traits
     * @param traitname displayName to identify and store trait
     */
    public void registerTrait(Class<? extends Trait> trait, String traitname) {

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(trait).withName(traitname));
    }

    /**
     * ATTENTION: load NPCs after you register all customs traits
     *
     * @param host displayName of the holder of the npcs, typically the plugin displayName
     *
     * @see this.registerTrait
     */
    public void loadNPCs(String host) {

        register.put(host, createNPCRegistry(host));
    }

    public void store(String host) {

        if(!stores.containsKey(host)) return;

        for (NPC npc : this.register.get(host)) {
            this.stores.get(host).store(npc);
        }
        this.stores.get(host).saveToDiskImmediate();
    }

    public void storeAll() {

        for (String host : this.stores.keySet()) {
            storeImmediate(host);
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

    public boolean isNPC(Entity entity) {

        return entity.hasMetadata(EntityMetaData.NPC);
    }

    /**
     * Clear and kill all NPC's of a non persistent host
     *
     * @param host non persistent host
     */

    public void clear(String host) {

        if (!nonPersistentRegistry.containsKey(host)) {
            return;
        }
        nonPersistentRegistry.get(host).forEach(npc -> npc.despawn(DespawnReason.REMOVAL));
    }

    public class NonPersitentNPCDataStore implements NPCDataStore {

        private int id = 0;

        @Override
        public void clearData(NPC npc) {
            // nothing
        }

        @Override
        public int createUniqueNPCId(NPCRegistry npcs) {

            id++;
            return id - 1;
        }

        @Override
        public void loadInto(NPCRegistry npcs) {
            // nothing
        }

        @Override
        public void saveToDisk() {
            // nothing
        }

        @Override
        public void saveToDiskImmediate() {
            // nothing
        }

        @Override
        public void store(NPC npc) {
            // nothing
        }

        @Override
        public void storeAll(NPCRegistry npcs) {
            // nothing
        }

        @Override
        public void reloadFromSource() {
            // nothing
        }
    }
}
